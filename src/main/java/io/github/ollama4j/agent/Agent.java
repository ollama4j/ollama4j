/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.agent;

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
import tools.jackson.dataformat.yaml.YAMLMapper;

public class Agent {
    private final String name;
    private final List<Tools.Tool> tools;
    private final Ollama ollamaClient;
    private final String model;
    private final List<OllamaChatMessage> chatHistory;
    private final String customPrompt;

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

    public static Agent fromYaml(String agentYaml) {
        try {
            YAMLMapper mapper = new YAMLMapper();
            InputStream input = Agent.class.getClassLoader().getResourceAsStream(agentYaml);
            if (input == null) {
                java.nio.file.Path filePath = java.nio.file.Paths.get(agentYaml);
                if (java.nio.file.Files.exists(filePath)) {
                    input = java.nio.file.Files.newInputStream(filePath);
                } else {
                    throw new RuntimeException(
                            agentYaml + " not found in classpath or file system");
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
            return new Agent(
                    agentSpec.getName(),
                    new Ollama(agentSpec.getHost()),
                    agentSpec.getModel(),
                    agentSpec.getCustomPrompt(),
                    agentTools);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load agent from YAML", e);
        }
    }

    public String think(String userInput) throws OllamaException {
        StringBuilder availableToolsDescription = new StringBuilder();
        if (!tools.isEmpty()) {
            for (Tools.Tool t : tools) {
                String toolName = t.getToolSpec().getName();
                String toolDescription = t.getToolSpec().getDescription();
                availableToolsDescription.append(
                        "\nTool name: '"
                                + toolName
                                + "'. Tool Description: '"
                                + toolDescription
                                + "'.\n");
            }
        }
        if (chatHistory.isEmpty()) {
            chatHistory.add(
                    //                    new OllamaChatMessage(
                    //                            OllamaChatMessageRole.SYSTEM,
                    //                            "You are a helpful assistant named "
                    //                                    + name
                    //                                    + ". You only perform tasks using tools
                    // available for you. You"
                    //                                    + " respond very precisely and you don't
                    // overthink or be too"
                    //                                    + " creative. Do not ever reveal the tool
                    // specification in"
                    //                                    + " terms of code or JSON or in a way that
                    // a software engineer"
                    //                                    + " sees it. Just be careful with your
                    // responses and respond"
                    //                                    + " like a human. Note that you only
                    // execute tools provided to"
                    //                                    + " you. Following are the tools that you
                    // have access to and"
                    //                                    + " you can perform right actions using
                    // right tools."
                    //                                    + availableToolsDescription));
                    new OllamaChatMessage(
                            OllamaChatMessageRole.SYSTEM,
                            "You are a helpful assistant named "
                                    + name
                                    + ". You only perform tasks using tools available for you. "
                                    + customPrompt
                                    + ". Following are the tools that you have access to and"
                                    + " you can perform right actions using right tools."
                                    + availableToolsDescription));
        }
        OllamaChatRequest request =
                OllamaChatRequest.builder()
                        .withTools(tools)
                        .withUseTools(true)
                        .withModel(model)
                        .withMessages(chatHistory)
                        .withMessage(OllamaChatMessageRole.USER, userInput)
                        .build();
        request.withMessage(OllamaChatMessageRole.USER, userInput);
        OllamaChatStreamObserver chatTokenHandler =
                new OllamaChatStreamObserver(
                        new ConsoleOutputGenerateTokenHandler(),
                        new ConsoleOutputGenerateTokenHandler());
        OllamaChatResult response = ollamaClient.chat(request, chatTokenHandler);
        chatHistory.clear();
        chatHistory.addAll(response.getChatHistory());
        return response.getResponseModel().getMessage().getResponse();
    }

    public void runInteractive() throws OllamaException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("\nYou: ");
            String input = sc.nextLine();
            if ("exit".equalsIgnoreCase(input)) break;
            String response = this.think(input);
        }
    }

    @Data
    public static class AgentSpec {
        private String name;
        private String description;
        private List<AgentToolSpec> tools;
        private Tools.Parameters parameters;
        private String host;
        private String model;
        private String customPrompt;
    }

    @Data
    @Setter
    @Getter
    private static class AgentToolSpec extends Tools.ToolSpec {
        private String toolFunctionFQCN = null;
        private ToolFunction toolFunctionInstance = null;
    }
}
