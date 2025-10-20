/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.ollama4j.Ollama;
import io.github.ollama4j.exceptions.OllamaException;
import io.github.ollama4j.impl.ConsoleOutputGenerateTokenHandler;
import io.github.ollama4j.models.chat.*;
import io.github.ollama4j.tools.ToolFunction;
import io.github.ollama4j.tools.Tools;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lombok.*;

/**
 * The {@code Agent} class represents an AI assistant capable of interacting with the Ollama API
 * server.
 *
 * <p>It supports the use of tools (interchangeable code components), persistent chat history, and
 * interactive as well as pre-scripted chat sessions.
 *
 * <h2>Usage</h2>
 *
 * <ul>
 *   <li>Instantiate an Agent via {@link #load(String)} for YAML-based configuration.
 *   <li>Handle conversation turns via {@link #interact(String, OllamaChatStreamObserver)}.
 *   <li>Use {@link #runInteractive()} for an interactive console-based session.
 * </ul>
 */
public class Agent {
    /**
     * The agent's display name
     */
    private final String name;

    /**
     * List of supported tools for this agent
     */
    private final List<Tools.Tool> tools;

    /**
     * Ollama client instance for communication with the API
     */
    private final Ollama ollamaClient;

    /**
     * The model name used for chat completions
     */
    private final String model;

    /**
     * Persists chat message history across rounds
     */
    private final List<OllamaChatMessage> chatHistory;

    /**
     * Optional custom system prompt for the agent
     */
    private final String customPrompt;

    /**
     * Constructs a new Agent.
     *
     * @param name         The agent's given name.
     * @param ollamaClient The Ollama API client instance to use.
     * @param model        The model name to use for chat completion.
     * @param customPrompt A custom prompt to prepend to all conversations (may be null).
     * @param tools        List of available tools for function calling.
     */
    public Agent(
            String name,
            Ollama ollamaClient,
            String model,
            String customPrompt,
            List<Tools.Tool> tools) {
        this.name = name;
        this.ollamaClient = ollamaClient;
        this.chatHistory = new ArrayList<>();
        this.tools = tools;
        this.model = model;
        this.customPrompt = customPrompt;
    }

    /**
     * Loads and constructs an Agent from a YAML configuration file (classpath or filesystem).
     *
     * <p>The YAML should define the agent, the model, and the desired tool functions (using their
     * fully qualified class names for auto-discovery).
     *
     * @param yamlPathOrResource Path or classpath resource name of the YAML file.
     * @return New Agent instance loaded according to the YAML definition.
     * @throws RuntimeException if the YAML cannot be read or agent cannot be constructed.
     */
    public static Agent load(String yamlPathOrResource) {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

            InputStream input =
                    Agent.class.getClassLoader().getResourceAsStream(yamlPathOrResource);
            if (input == null) {
                java.nio.file.Path filePath = java.nio.file.Paths.get(yamlPathOrResource);
                if (java.nio.file.Files.exists(filePath)) {
                    input = java.nio.file.Files.newInputStream(filePath);
                } else {
                    throw new RuntimeException(
                            yamlPathOrResource + " not found in classpath or file system");
                }
            }
            AgentSpec agentSpec = mapper.readValue(input, AgentSpec.class);
            List<AgentToolSpec> tools = agentSpec.getTools();
            for (AgentToolSpec tool : tools) {
                String fqcn = tool.getToolFunctionFQCN();
                if (fqcn != null && !fqcn.isEmpty()) {
                    try {
                        Class<?> clazz = Class.forName(fqcn);
                        Object instance = clazz.getDeclaredConstructor().newInstance();
                        if (instance instanceof ToolFunction) {
                            tool.setToolFunctionInstance((ToolFunction) instance);
                        } else {
                            throw new RuntimeException(
                                    "Class does not implement ToolFunction: " + fqcn);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(
                                "Failed to instantiate tool function: " + fqcn, e);
                    }
                }
            }
            List<Tools.Tool> agentTools = new ArrayList<>();
            for (AgentToolSpec a : tools) {
                Tools.Tool t = new Tools.Tool();
                t.setToolFunction(a.getToolFunctionInstance());
                Tools.ToolSpec ts = new Tools.ToolSpec();
                ts.setName(a.getName());
                ts.setDescription(a.getDescription());
                ts.setParameters(a.getParameters());
                t.setToolSpec(ts);
                agentTools.add(t);
            }
            Ollama ollama = new Ollama(agentSpec.getHost());
            ollama.setRequestTimeoutSeconds(120);
            return new Agent(
                    agentSpec.getName(),
                    ollama,
                    agentSpec.getModel(),
                    agentSpec.getCustomPrompt(),
                    agentTools);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load agent from YAML", e);
        }
    }

    /**
     * Facilitates a single round of chat for the agent:
     *
     * <ul>
     *   <li>Builds/promotes the system prompt on the first turn if necessary
     *   <li>Adds the user's input to chat history
     *   <li>Submits the chat turn to the Ollama model (with tool/function support)
     *   <li>Updates internal chat history in accordance with the Ollama chat result
     * </ul>
     *
     * @param userInput The user's message or question for the agent.
     * @return The model's response as a string.
     * @throws OllamaException If there is a problem with the Ollama API.
     */
    public String interact(String userInput, OllamaChatStreamObserver chatTokenHandler)
            throws OllamaException {
        // Build a concise and readable description of available tools
        String availableToolsDescription =
                tools.isEmpty()
                        ? ""
                        : tools.stream()
                                .map(
                                        t ->
                                                String.format(
                                                        "- %s: %s",
                                                        t.getToolSpec().getName(),
                                                        t.getToolSpec().getDescription() != null
                                                                ? t.getToolSpec().getDescription()
                                                                : "No description"))
                                .reduce((a, b) -> a + "\n" + b)
                                .map(desc -> "\nYou have access to the following tools:\n" + desc)
                                .orElse("");

        // Add system prompt if chatHistory is empty
        if (chatHistory.isEmpty()) {
            String systemPrompt =
                    String.format(
                            "You are a helpful AI assistant named %s. Your actions are limited to"
                                    + " using the available tools. %s%s",
                            name,
                            (customPrompt != null ? customPrompt : ""),
                            availableToolsDescription);
            chatHistory.add(new OllamaChatMessage(OllamaChatMessageRole.SYSTEM, systemPrompt));
        }

        // Add the user input as a message before sending request
        chatHistory.add(new OllamaChatMessage(OllamaChatMessageRole.USER, userInput));

        OllamaChatRequest request =
                OllamaChatRequest.builder()
                        .withTools(tools)
                        .withUseTools(true)
                        .withModel(model)
                        .withMessages(chatHistory)
                        .build();
        OllamaChatResult response = ollamaClient.chat(request, chatTokenHandler);

        // Update chat history for continuity
        chatHistory.clear();
        chatHistory.addAll(response.getChatHistory());

        return response.getResponseModel().getMessage().getResponse();
    }

    /**
     * Launches an endless interactive console session with the agent, echoing user input and the
     * agent's response using the provided chat model and tools.
     *
     * <p>Type {@code exit} to break the loop and terminate the session.
     *
     * @throws OllamaException if any errors occur talking to the Ollama API.
     */
    public void runInteractive() throws OllamaException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("\n[You]: ");
            String input = sc.nextLine();
            if ("exit".equalsIgnoreCase(input)) break;
            this.interact(
                    input,
                    new OllamaChatStreamObserver(
                            new ConsoleOutputGenerateTokenHandler(),
                            new ConsoleOutputGenerateTokenHandler()));
        }
    }

    /**
     * Bean describing an agent as definable from YAML.
     *
     * <ul>
     *   <li>{@code name}: Agent display name
     *   <li>{@code description}: Freeform description
     *   <li>{@code tools}: List of tools/functions to enable
     *   <li>{@code host}: Target Ollama host address
     *   <li>{@code model}: Name of Ollama model to use
     *   <li>{@code customPrompt}: Agent's custom base prompt
     *   <li>{@code requestTimeoutSeconds}: Timeout for requests
     * </ul>
     */
    @Data
    public static class AgentSpec {
        private String name;
        private String description;
        private List<AgentToolSpec> tools;
        private String host;
        private String model;
        private String customPrompt;
        private int requestTimeoutSeconds;
    }

    /**
     * Subclass extension of {@link Tools.ToolSpec}, which allows associating a tool with a function
     * implementation (via FQCN).
     */
    @Data
    @Setter
    @Getter
    @EqualsAndHashCode(callSuper = false)
    private static class AgentToolSpec extends Tools.ToolSpec {
        /**
         * Fully qualified class name of the tool's {@link ToolFunction} implementation
         */
        private String toolFunctionFQCN = null;

        /**
         * Instance of the {@link ToolFunction} to invoke
         */
        private ToolFunction toolFunctionInstance = null;
    }

    /**
     * Bean for describing a tool function parameter for use in agent YAML definitions.
     */
    @Data
    public class AgentToolParameter {
        /**
         * The parameter's type (e.g., string, number, etc.)
         */
        private String type;

        /**
         * Description of the parameter
         */
        private String description;

        /**
         * Whether this parameter is required
         */
        private boolean required;

        /**
         * Enum values (if any) that this parameter may take; _enum used because 'enum' is reserved
         */
        private List<String> _enum; // `enum` is a reserved keyword, so use _enum or similar
    }
}
