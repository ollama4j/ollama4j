/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.integrationtests;

import static org.junit.jupiter.api.Assertions.*;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.exceptions.ToolInvocationException;
import io.github.ollama4j.impl.ConsoleOutputChatTokenHandler;
import io.github.ollama4j.impl.ConsoleOutputGenerateTokenHandler;
import io.github.ollama4j.models.chat.*;
import io.github.ollama4j.models.embeddings.OllamaEmbedRequestModel;
import io.github.ollama4j.models.embeddings.OllamaEmbedResponseModel;
import io.github.ollama4j.models.generate.OllamaGenerateStreamObserver;
import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.models.response.ModelDetail;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.samples.AnnotatedTool;
import io.github.ollama4j.tools.OllamaToolCallsFunction;
import io.github.ollama4j.tools.ToolFunction;
import io.github.ollama4j.tools.Tools;
import io.github.ollama4j.tools.annotations.OllamaToolService;
import io.github.ollama4j.utils.OptionsBuilder;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.ollama.OllamaContainer;

@OllamaToolService(providers = {AnnotatedTool.class})
@TestMethodOrder(OrderAnnotation.class)
@SuppressWarnings({"HttpUrlsUsage", "SpellCheckingInspection", "FieldCanBeLocal", "ConstantValue"})
class OllamaAPIIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(OllamaAPIIntegrationTest.class);

    private static OllamaContainer ollama;
    private static OllamaAPI api;

    private static final String EMBEDDING_MODEL = "all-minilm";
    private static final String VISION_MODEL = "moondream:1.8b";
    private static final String THINKING_TOOL_MODEL = "deepseek-r1:1.5b";
    private static final String THINKING_TOOL_MODEL_2 = "qwen3:0.6b";
    private static final String GENERAL_PURPOSE_MODEL = "gemma3:270m";
    private static final String TOOLS_MODEL = "mistral:7b";

    /**
     * Initializes the OllamaAPI instance for integration tests.
     *
     * <p>This method sets up the OllamaAPI client, either using an external Ollama host (if
     * environment variables are set) or by starting a Testcontainers-based Ollama instance. It also
     * configures request timeout and model pull retry settings.
     */
    @BeforeAll
    static void setUp() {
        // ... (no javadoc needed for private setup logic)
        int requestTimeoutSeconds = 60;
        int numberOfRetriesForModelPull = 5;
        int modelKeepAliveTime = 0;

        try {
            String useExternalOllamaHostEnv = System.getenv("USE_EXTERNAL_OLLAMA_HOST");
            String ollamaHostEnv = System.getenv("OLLAMA_HOST");

            boolean useExternalOllamaHost;
            String ollamaHost;

            if (useExternalOllamaHostEnv == null && ollamaHostEnv == null) {
                Properties props = new Properties();
                try {
                    props.load(
                            OllamaAPIIntegrationTest.class
                                    .getClassLoader()
                                    .getResourceAsStream("test-config.properties"));
                } catch (Exception e) {
                    throw new RuntimeException(
                            "Could not load test-config.properties from classpath", e);
                }
                useExternalOllamaHost =
                        Boolean.parseBoolean(
                                props.getProperty("USE_EXTERNAL_OLLAMA_HOST", "false"));
                ollamaHost = props.getProperty("OLLAMA_HOST");
                requestTimeoutSeconds =
                        Integer.parseInt(props.getProperty("REQUEST_TIMEOUT_SECONDS"));
                numberOfRetriesForModelPull =
                        Integer.parseInt(props.getProperty("NUMBER_RETRIES_FOR_MODEL_PULL"));
                modelKeepAliveTime = Integer.parseInt(props.getProperty("MODEL_KEEP_ALIVE_TIME"));
            } else {
                useExternalOllamaHost = Boolean.parseBoolean(useExternalOllamaHostEnv);
                ollamaHost = ollamaHostEnv;
            }

            if (useExternalOllamaHost) {
                LOG.info("Using external Ollama host...");
                api = new OllamaAPI(ollamaHost);
            } else {
                throw new RuntimeException(
                        "USE_EXTERNAL_OLLAMA_HOST is not set so, we will be using Testcontainers"
                            + " Ollama host for the tests now. If you would like to use an external"
                            + " host, please set the env var to USE_EXTERNAL_OLLAMA_HOST=true and"
                            + " set the env var OLLAMA_HOST=http://localhost:11435 or a different"
                            + " host/port.");
            }
        } catch (Exception e) {
            String ollamaVersion = "0.6.1";
            int internalPort = 11434;
            int mappedPort = 11435;
            ollama = new OllamaContainer("ollama/ollama:" + ollamaVersion);
            ollama.addExposedPort(internalPort);
            List<String> portBindings = new ArrayList<>();
            portBindings.add(mappedPort + ":" + internalPort);
            ollama.setPortBindings(portBindings);
            ollama.start();
            LOG.info("Using Testcontainer Ollama host...");
            api =
                    new OllamaAPI(
                            "http://"
                                    + ollama.getHost()
                                    + ":"
                                    + ollama.getMappedPort(internalPort));
        }
        api.setRequestTimeoutSeconds(requestTimeoutSeconds);
        api.setNumberOfRetriesForModelPull(numberOfRetriesForModelPull);
        api.setModelKeepAliveTime(modelKeepAliveTime);
    }

    /**
     * Verifies that a ConnectException is thrown when attempting to connect to a non-existent
     * Ollama endpoint.
     *
     * <p>Scenario: Ensures the API client fails gracefully when the Ollama server is unreachable.
     */
    @Test
    @Order(1)
    void shouldThrowConnectExceptionForWrongEndpoint() {
        OllamaAPI ollamaAPI = new OllamaAPI("http://wrong-host:11434");
        assertThrows(ConnectException.class, ollamaAPI::listModels);
    }

    /**
     * Tests retrieval of the Ollama server version.
     *
     * <p>Scenario: Calls the /api/version endpoint and asserts a non-null version string is
     * returned.
     */
    @Test
    @Order(1)
    void shouldReturnVersionFromVersionAPI()
            throws URISyntaxException, IOException, OllamaBaseException, InterruptedException {
        String version = api.getVersion();
        assertNotNull(version);
    }

    /**
     * Tests the /api/ping endpoint for server liveness.
     *
     * <p>Scenario: Ensures the Ollama server responds to ping requests.
     */
    @Test
    @Order(1)
    void shouldPingSuccessfully() throws OllamaBaseException {
        boolean pingResponse = api.ping();
        assertTrue(pingResponse, "Ping should return true");
    }

    /**
     * Tests listing all available models from the Ollama server.
     *
     * <p>Scenario: Calls /api/tags and verifies the returned list is not null (may be empty).
     */
    @Test
    @Order(2)
    void shouldListModels()
            throws URISyntaxException, IOException, OllamaBaseException, InterruptedException {
        List<Model> models = api.listModels();
        assertNotNull(models, "Models should not be null");
        assertTrue(models.size() >= 0, "Models list can be empty or contain elements");
    }

    @Test
    @Order(2)
    void shouldUnloadModel()
            throws URISyntaxException, IOException, OllamaBaseException, InterruptedException {
        final String model = "all-minilm:latest";
        api.unloadModel(model);
        boolean isUnloaded =
                api.ps().getModels().stream().noneMatch(mp -> model.equals(mp.getName()));
        assertTrue(isUnloaded, "Model should be unloaded but is still present in process list");
    }

    /**
     * Tests pulling a model and verifying it appears in the model list.
     *
     * <p>Scenario: Pulls an embedding model, then checks that it is present in the list of models.
     */
    @Test
    @Order(3)
    void shouldPullModelAndListModels()
            throws URISyntaxException, IOException, OllamaBaseException, InterruptedException {
        api.pullModel(EMBEDDING_MODEL);
        List<Model> models = api.listModels();
        assertNotNull(models, "Models should not be null");
        assertFalse(models.isEmpty(), "Models list should contain elements");
    }

    /**
     * Tests fetching detailed information for a specific model.
     *
     * <p>Scenario: Pulls a model and retrieves its details, asserting the model file contains the
     * model name.
     */
    @Test
    @Order(4)
    void shouldGetModelDetails()
            throws IOException, OllamaBaseException, URISyntaxException, InterruptedException {
        api.pullModel(EMBEDDING_MODEL);
        ModelDetail modelDetails = api.getModelDetails(EMBEDDING_MODEL);
        assertNotNull(modelDetails);
        assertTrue(modelDetails.getModelFile().contains(EMBEDDING_MODEL));
    }

    /**
     * Tests generating embeddings for a batch of input texts.
     *
     * <p>Scenario: Uses the embedding model to generate vector embeddings for two input sentences.
     */
    @Test
    @Order(5)
    void shouldReturnEmbeddings() throws Exception {
        api.pullModel(EMBEDDING_MODEL);
        OllamaEmbedRequestModel m = new OllamaEmbedRequestModel();
        m.setModel(EMBEDDING_MODEL);
        m.setInput(Arrays.asList("Why is the sky blue?", "Why is the grass green?"));
        OllamaEmbedResponseModel embeddings = api.embed(m);
        assertNotNull(embeddings, "Embeddings should not be null");
        assertFalse(embeddings.getEmbeddings().isEmpty(), "Embeddings should not be empty");
    }

    /**
     * Tests generating structured output using the 'format' parameter.
     *
     * <p>Scenario: Calls generateWithFormat with a prompt and a JSON schema, expecting a structured
     * response. Usage: generate with format, no thinking, no streaming.
     */
    @Test
    @Order(6)
    void shouldGenerateWithStructuredOutput()
            throws OllamaBaseException, IOException, InterruptedException, URISyntaxException {
        api.pullModel(TOOLS_MODEL);

        String prompt =
                "The sun is shining brightly and is directly overhead at the zenith, casting my"
                        + " shadow over my foot, so it must be noon.";

        Map<String, Object> format = new HashMap<>();
        format.put("type", "object");
        format.put(
                "properties",
                new HashMap<String, Object>() {
                    {
                        put(
                                "isNoon",
                                new HashMap<String, Object>() {
                                    {
                                        put("type", "boolean");
                                    }
                                });
                    }
                });
        format.put("required", List.of("isNoon"));

        OllamaResult result = api.generateWithFormat(TOOLS_MODEL, prompt, format);

        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
        assertNotNull(result.getStructuredResponse().get("isNoon"));
    }

    /**
     * Tests basic text generation with default options.
     *
     * <p>Scenario: Calls generate with a general-purpose model, no thinking, no streaming, no
     * format. Usage: generate, raw=false, think=false, no streaming.
     */
    @Test
    @Order(6)
    void shouldGenerateWithDefaultOptions()
            throws OllamaBaseException, IOException, InterruptedException, URISyntaxException {
        api.pullModel(GENERAL_PURPOSE_MODEL);
        boolean raw = false;
        boolean thinking = false;
        OllamaResult result =
                api.generate(
                        GENERAL_PURPOSE_MODEL,
                        "What is the capital of France? And what's France's connection with Mona"
                                + " Lisa?",
                        raw,
                        thinking,
                        new OptionsBuilder().build(),
                        new OllamaGenerateStreamObserver(null, null));
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
    }

    /**
     * Tests text generation with streaming enabled.
     *
     * <p>Scenario: Calls generate with a general-purpose model, streaming the response tokens.
     * Usage: generate, raw=false, think=false, streaming enabled.
     */
    @Test
    @Order(7)
    void shouldGenerateWithDefaultOptionsStreamed()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(GENERAL_PURPOSE_MODEL);
        boolean raw = false;
        OllamaResult result =
                api.generate(
                        GENERAL_PURPOSE_MODEL,
                        "What is the capital of France? And what's France's connection with Mona"
                                + " Lisa?",
                        raw,
                        false,
                        new OptionsBuilder().build(),
                        new OllamaGenerateStreamObserver(
                                null, new ConsoleOutputGenerateTokenHandler()));

        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
    }

    /**
     * Tests chat API with custom options (e.g., temperature).
     *
     * <p>Scenario: Builds a chat request with system and user messages, sets a custom temperature,
     * and verifies the response. Usage: chat, no tools, no thinking, no streaming, custom options.
     */
    @Test
    @Order(8)
    void shouldGenerateWithCustomOptions()
            throws OllamaBaseException,
                    IOException,
                    URISyntaxException,
                    InterruptedException,
                    ToolInvocationException {
        api.pullModel(GENERAL_PURPOSE_MODEL);

        OllamaChatRequestBuilder builder =
                OllamaChatRequestBuilder.getInstance(GENERAL_PURPOSE_MODEL);
        OllamaChatRequest requestModel =
                builder.withMessage(
                                OllamaChatMessageRole.SYSTEM,
                                "You are a helpful assistant who can generate random person's first"
                                        + " and last names in the format [First name, Last name].")
                        .build();
        requestModel =
                builder.withMessages(requestModel.getMessages())
                        .withMessage(OllamaChatMessageRole.USER, "Give me a cool name")
                        .withOptions(new OptionsBuilder().setTemperature(0.5f).build())
                        .build();
        OllamaChatResult chatResult = api.chat(requestModel, null);

        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertFalse(chatResult.getResponseModel().getMessage().getResponse().isEmpty());
    }

    /**
     * Tests chat API with a system prompt and verifies the assistant's response.
     *
     * <p>Scenario: Sends a system prompt instructing the assistant to reply with a specific word,
     * then checks the response. Usage: chat, no tools, no thinking, no streaming, system prompt.
     */
    @Test
    @Order(9)
    void shouldChatWithSystemPrompt()
            throws OllamaBaseException,
                    IOException,
                    URISyntaxException,
                    InterruptedException,
                    ToolInvocationException {
        api.pullModel(GENERAL_PURPOSE_MODEL);

        String expectedResponse = "Bhai";

        OllamaChatRequestBuilder builder =
                OllamaChatRequestBuilder.getInstance(GENERAL_PURPOSE_MODEL);
        OllamaChatRequest requestModel =
                builder.withMessage(
                                OllamaChatMessageRole.SYSTEM,
                                String.format(
                                        "[INSTRUCTION-START] You are an obidient and helpful bot"
                                            + " named %s. You always answer with only one word and"
                                            + " that word is your name. [INSTRUCTION-END]",
                                        expectedResponse))
                        .withMessage(OllamaChatMessageRole.USER, "Who are you?")
                        .withOptions(new OptionsBuilder().setTemperature(0.0f).build())
                        .build();

        OllamaChatResult chatResult = api.chat(requestModel, null);
        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertNotNull(chatResult.getResponseModel().getMessage());
        assertFalse(chatResult.getResponseModel().getMessage().getResponse().isBlank());
        assertTrue(
                chatResult
                        .getResponseModel()
                        .getMessage()
                        .getResponse()
                        .contains(expectedResponse));
        assertEquals(3, chatResult.getChatHistory().size());
    }

    /**
     * Tests chat API with multi-turn conversation (chat history).
     *
     * <p>Scenario: Sends a sequence of user messages, each time including the chat history, and
     * verifies the assistant's responses. Usage: chat, no tools, no thinking, no streaming,
     * multi-turn.
     */
    @Test
    @Order(10)
    void shouldChatWithHistory() throws Exception {
        api.pullModel(THINKING_TOOL_MODEL);
        OllamaChatRequestBuilder builder =
                OllamaChatRequestBuilder.getInstance(THINKING_TOOL_MODEL);

        OllamaChatRequest requestModel =
                builder.withMessage(
                                OllamaChatMessageRole.USER, "What is 1+1? Answer only in numbers.")
                        .build();

        OllamaChatResult chatResult = api.chat(requestModel, null);

        assertNotNull(chatResult);
        assertNotNull(chatResult.getChatHistory());
        assertNotNull(chatResult.getChatHistory().stream());

        requestModel =
                builder.withMessages(chatResult.getChatHistory())
                        .withMessage(OllamaChatMessageRole.USER, "And what is its squared value?")
                        .build();

        chatResult = api.chat(requestModel, null);

        assertNotNull(chatResult);
        assertNotNull(chatResult.getChatHistory());
        assertNotNull(chatResult.getChatHistory().stream());

        requestModel =
                builder.withMessages(chatResult.getChatHistory())
                        .withMessage(
                                OllamaChatMessageRole.USER,
                                "What is the largest value between 2, 4 and 6?")
                        .build();

        chatResult = api.chat(requestModel, null);

        assertNotNull(chatResult, "Chat result should not be null");
        assertTrue(
                chatResult.getChatHistory().size() > 2,
                "Chat history should contain more than two messages");
    }

    /**
     * Tests chat API with explicit tool invocation (client does not handle tools).
     *
     * <p>Scenario: Registers a tool, sends a user message that triggers a tool call, and verifies
     * the tool call and arguments. Usage: chat, explicit tool, useTools=false, no thinking, no
     * streaming.
     */
    @Test
    @Order(11)
    void shouldChatWithExplicitTool()
            throws OllamaBaseException,
                    IOException,
                    URISyntaxException,
                    InterruptedException,
                    ToolInvocationException {
        String theToolModel = TOOLS_MODEL;
        api.pullModel(theToolModel);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(theToolModel);

        api.registerTool(employeeFinderTool());

        OllamaChatRequest requestModel =
                builder.withMessage(
                                OllamaChatMessageRole.USER,
                                "Give me the ID and address of the employee Rahul Kumar.")
                        .build();
        requestModel.setOptions(new OptionsBuilder().setTemperature(0.9f).build().getOptionsMap());
        requestModel.setUseTools(true);
        OllamaChatResult chatResult = api.chat(requestModel, null);

        assertNotNull(chatResult, "chatResult should not be null");
        assertNotNull(chatResult.getResponseModel(), "Response model should not be null");
        assertNotNull(
                chatResult.getResponseModel().getMessage(), "Response message should not be null");
        assertEquals(
                OllamaChatMessageRole.ASSISTANT.getRoleName(),
                chatResult.getResponseModel().getMessage().getRole().getRoleName(),
                "Role of the response message should be ASSISTANT");
        List<OllamaChatToolCalls> toolCalls = chatResult.getChatHistory().get(1).getToolCalls();
        assert (!toolCalls.isEmpty());
        OllamaToolCallsFunction function = toolCalls.get(0).getFunction();
        assertEquals(
                "get-employee-details",
                function.getName(),
                "Tool function name should be 'get-employee-details'");
        assertFalse(
                function.getArguments().isEmpty(), "Tool function arguments should not be empty");
        Object employeeName = function.getArguments().get("employee-name");
        assertNotNull(employeeName, "Employee name argument should not be null");
        assertEquals("Rahul Kumar", employeeName, "Employee name argument should be 'Rahul Kumar'");
        assertTrue(
                chatResult.getChatHistory().size() > 2,
                "Chat history should have more than 2 messages");
        List<OllamaChatToolCalls> finalToolCalls =
                chatResult.getResponseModel().getMessage().getToolCalls();
        assertNull(finalToolCalls, "Final tool calls in the response message should be null");
    }

    /**
     * Tests chat API with explicit tool invocation and useTools=true.
     *
     * <p>Scenario: Registers a tool, enables useTools, sends a user message, and verifies the
     * assistant's tool call. Usage: chat, explicit tool, useTools=true, no thinking, no streaming.
     */
    @Test
    @Order(13)
    void shouldChatWithExplicitToolAndUseTools()
            throws OllamaBaseException,
                    IOException,
                    URISyntaxException,
                    InterruptedException,
                    ToolInvocationException {
        String theToolModel = TOOLS_MODEL;
        api.pullModel(theToolModel);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(theToolModel);

        api.registerTool(employeeFinderTool());

        OllamaChatRequest requestModel =
                builder.withMessage(
                                OllamaChatMessageRole.USER,
                                "Give me the ID and address of the employee Rahul Kumar.")
                        .build();
        requestModel.setOptions(new OptionsBuilder().setTemperature(0.9f).build().getOptionsMap());
        requestModel.setUseTools(true);
        OllamaChatResult chatResult = api.chat(requestModel, null);

        assertNotNull(chatResult, "chatResult should not be null");
        assertNotNull(chatResult.getResponseModel(), "Response model should not be null");
        assertNotNull(
                chatResult.getResponseModel().getMessage(), "Response message should not be null");
        assertEquals(
                OllamaChatMessageRole.ASSISTANT.getRoleName(),
                chatResult.getResponseModel().getMessage().getRole().getRoleName(),
                "Role of the response message should be ASSISTANT");

        boolean toolCalled = false;
        List<OllamaChatMessage> msgs = chatResult.getChatHistory();
        for (OllamaChatMessage msg : msgs) {
            if (msg.getRole().equals(OllamaChatMessageRole.TOOL)) {
                toolCalled = true;
            }
        }
        assertTrue(toolCalled, "Assistant message should contain tool calls when useTools is true");
    }

    /**
     * Tests chat API with explicit tool invocation and streaming enabled.
     *
     * <p>Scenario: Registers a tool, sends a user message, and streams the assistant's response
     * (with tool call). Usage: chat, explicit tool, useTools=false, streaming enabled.
     */
    @Test
    @Order(14)
    void shouldChatWithToolsAndStream()
            throws OllamaBaseException,
                    IOException,
                    URISyntaxException,
                    InterruptedException,
                    ToolInvocationException {
        String theToolModel = TOOLS_MODEL;
        api.pullModel(theToolModel);

        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(theToolModel);

        api.registerTool(employeeFinderTool());

        OllamaChatRequest requestModel =
                builder.withMessage(
                                OllamaChatMessageRole.USER,
                                "Give me the ID and address of employee Rahul Kumar")
                        .withKeepAlive("0m")
                        .withOptions(new OptionsBuilder().setTemperature(0.9f).build())
                        .build();
        requestModel.setUseTools(true);
        OllamaChatResult chatResult = api.chat(requestModel, new ConsoleOutputChatTokenHandler());

        assertNotNull(chatResult, "chatResult should not be null");
        assertNotNull(chatResult.getResponseModel(), "Response model should not be null");
        assertNotNull(
                chatResult.getResponseModel().getMessage(), "Response message should not be null");
        assertEquals(
                OllamaChatMessageRole.ASSISTANT.getRoleName(),
                chatResult.getResponseModel().getMessage().getRole().getRoleName(),
                "Role of the response message should be ASSISTANT");
        List<OllamaChatToolCalls> toolCalls = chatResult.getChatHistory().get(1).getToolCalls();
        assertEquals(
                1,
                toolCalls.size(),
                "There should be exactly one tool call in the second chat history message");
        OllamaToolCallsFunction function = toolCalls.get(0).getFunction();
        assertEquals(
                "get-employee-details",
                function.getName(),
                "Tool function name should be 'get-employee-details'");
        assertFalse(
                function.getArguments().isEmpty(), "Tool function arguments should not be empty");
        assertTrue(
                chatResult.getChatHistory().size() > 2,
                "Chat history should have more than 2 messages");
        List<OllamaChatToolCalls> finalToolCalls =
                chatResult.getResponseModel().getMessage().getToolCalls();
        assertNull(finalToolCalls, "Final tool calls in the response message should be null");
    }

    /**
     * Tests chat API with an annotated tool (single parameter).
     *
     * <p>Scenario: Registers annotated tools, sends a user message that triggers a tool call, and
     * verifies the tool call and arguments. Usage: chat, annotated tool, no thinking, no streaming.
     */
    @Test
    @Order(12)
    void shouldChatWithAnnotatedToolSingleParam()
            throws OllamaBaseException,
                    IOException,
                    InterruptedException,
                    URISyntaxException,
                    ToolInvocationException {
        String theToolModel = TOOLS_MODEL;
        api.pullModel(theToolModel);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(theToolModel);

        api.registerAnnotatedTools();

        OllamaChatRequest requestModel =
                builder.withMessage(
                                OllamaChatMessageRole.USER,
                                "Compute the most important constant in the world using 5 digits")
                        .build();

        OllamaChatResult chatResult = api.chat(requestModel, null);
        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertNotNull(chatResult.getResponseModel().getMessage());
        assertEquals(
                OllamaChatMessageRole.ASSISTANT.getRoleName(),
                chatResult.getResponseModel().getMessage().getRole().getRoleName());
        List<OllamaChatToolCalls> toolCalls = chatResult.getChatHistory().get(1).getToolCalls();
        assert (!toolCalls.isEmpty());
        OllamaToolCallsFunction function = toolCalls.get(0).getFunction();
        assertEquals("computeImportantConstant", function.getName());
        assert (!function.getArguments().isEmpty());
        Object noOfDigits = function.getArguments().get("noOfDigits");
        assertNotNull(noOfDigits);
        assertEquals("5", noOfDigits.toString());
        assertTrue(chatResult.getChatHistory().size() > 2);
        List<OllamaChatToolCalls> finalToolCalls =
                chatResult.getResponseModel().getMessage().getToolCalls();
        assertNull(finalToolCalls);
    }

    /**
     * Tests chat API with an annotated tool (multiple parameters).
     *
     * <p>Scenario: Registers annotated tools, sends a user message that may trigger a tool call
     * with multiple arguments. Usage: chat, annotated tool, no thinking, no streaming, multiple
     * parameters.
     *
     * <p>Note: This test is non-deterministic due to model variability; some assertions are
     * commented out.
     */
    @Test
    @Order(13)
    void shouldChatWithAnnotatedToolMultipleParams()
            throws OllamaBaseException,
                    IOException,
                    URISyntaxException,
                    InterruptedException,
                    ToolInvocationException {
        String theToolModel = TOOLS_MODEL;
        api.pullModel(theToolModel);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(theToolModel);

        api.registerAnnotatedTools(new AnnotatedTool());

        OllamaChatRequest requestModel =
                builder.withMessage(
                                OllamaChatMessageRole.USER,
                                "Greet Rahul with a lot of hearts and respond to me with count of"
                                        + " emojis that have been in used in the greeting")
                        .build();

        OllamaChatResult chatResult = api.chat(requestModel, null);
        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertNotNull(chatResult.getResponseModel().getMessage());
        assertEquals(
                OllamaChatMessageRole.ASSISTANT.getRoleName(),
                chatResult.getResponseModel().getMessage().getRole().getRoleName());
    }

    /**
     * Tests chat API with streaming enabled (no tools, no thinking).
     *
     * <p>Scenario: Sends a user message and streams the assistant's response. Usage: chat, no
     * tools, no thinking, streaming enabled.
     */
    @Test
    @Order(15)
    void shouldChatWithStream()
            throws OllamaBaseException,
                    IOException,
                    URISyntaxException,
                    InterruptedException,
                    ToolInvocationException {
        api.deregisterTools();
        api.pullModel(GENERAL_PURPOSE_MODEL);
        OllamaChatRequestBuilder builder =
                OllamaChatRequestBuilder.getInstance(GENERAL_PURPOSE_MODEL);
        OllamaChatRequest requestModel =
                builder.withMessage(
                                OllamaChatMessageRole.USER,
                                "What is the capital of France? And what's France's connection with"
                                        + " Mona Lisa?")
                        .build();
        requestModel.setThink(false);

        OllamaChatResult chatResult = api.chat(requestModel, new ConsoleOutputChatTokenHandler());
        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertNotNull(chatResult.getResponseModel().getMessage());
        assertNotNull(chatResult.getResponseModel().getMessage().getResponse());
    }

    /**
     * Tests chat API with thinking and streaming enabled.
     *
     * <p>Scenario: Sends a user message with thinking enabled and streams the assistant's response.
     * Usage: chat, no tools, thinking enabled, streaming enabled.
     */
    @Test
    @Order(15)
    void shouldChatWithThinkingAndStream()
            throws OllamaBaseException,
                    IOException,
                    URISyntaxException,
                    InterruptedException,
                    ToolInvocationException {
        api.pullModel(THINKING_TOOL_MODEL_2);
        OllamaChatRequestBuilder builder =
                OllamaChatRequestBuilder.getInstance(THINKING_TOOL_MODEL_2);
        OllamaChatRequest requestModel =
                builder.withMessage(
                                OllamaChatMessageRole.USER,
                                "What is the capital of France? And what's France's connection with"
                                        + " Mona Lisa?")
                        .withThinking(true)
                        .withKeepAlive("0m")
                        .build();

        OllamaChatResult chatResult = api.chat(requestModel, new ConsoleOutputChatTokenHandler());

        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertNotNull(chatResult.getResponseModel().getMessage());
        assertNotNull(chatResult.getResponseModel().getMessage().getResponse());
    }

    /**
     * Tests chat API with an image input from a URL.
     *
     * <p>Scenario: Sends a user message with an image URL and verifies the assistant's response.
     * Usage: chat, vision model, image from URL, no tools, no thinking, no streaming.
     */
    @Test
    @Order(10)
    void shouldChatWithImageFromURL()
            throws OllamaBaseException,
                    IOException,
                    InterruptedException,
                    URISyntaxException,
                    ToolInvocationException {
        api.pullModel(VISION_MODEL);

        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(VISION_MODEL);
        OllamaChatRequest requestModel =
                builder.withMessage(
                                OllamaChatMessageRole.USER,
                                "What's in the picture?",
                                Collections.emptyList(),
                                "https://t3.ftcdn.net/jpg/02/96/63/80/360_F_296638053_0gUVA4WVBKceGsIr7LNqRWSnkusi07dq.jpg")
                        .build();
        api.registerAnnotatedTools(new OllamaAPIIntegrationTest());

        OllamaChatResult chatResult = api.chat(requestModel, null);
        assertNotNull(chatResult);
    }

    /**
     * Tests chat API with an image input from a file and multi-turn history.
     *
     * <p>Scenario: Sends a user message with an image file, then continues the conversation with
     * chat history. Usage: chat, vision model, image from file, multi-turn, no tools, no thinking,
     * no streaming.
     */
    @Test
    @Order(10)
    void shouldChatWithImageFromFileAndHistory()
            throws OllamaBaseException,
                    IOException,
                    URISyntaxException,
                    InterruptedException,
                    ToolInvocationException {
        api.pullModel(VISION_MODEL);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(VISION_MODEL);
        OllamaChatRequest requestModel =
                builder.withMessage(
                                OllamaChatMessageRole.USER,
                                "What's in the picture?",
                                Collections.emptyList(),
                                List.of(getImageFileFromClasspath("emoji-smile.jpeg")))
                        .build();

        OllamaChatResult chatResult = api.chat(requestModel, null);
        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        builder.reset();

        requestModel =
                builder.withMessages(chatResult.getChatHistory())
                        .withMessage(OllamaChatMessageRole.USER, "What's the color?")
                        .build();

        chatResult = api.chat(requestModel, null);
        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
    }

    /**
     * Tests generateWithImages using an image URL as input.
     *
     * <p>Scenario: Calls generateWithImages with a vision model and an image URL, expecting a
     * non-empty response. Usage: generateWithImages, image from URL, no streaming.
     */
    @Test
    @Order(17)
    void shouldGenerateWithImageURLs()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(VISION_MODEL);

        OllamaResult result =
                api.generateWithImages(
                        VISION_MODEL,
                        "What is in this image?",
                        List.of(
                                "https://i.pinimg.com/736x/f9/4e/cb/f94ecba040696a3a20b484d2e15159ec.jpg"),
                        new OptionsBuilder().build(),
                        null,
                        null);
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
    }

    /**
     * Tests generateWithImages using an image file as input.
     *
     * <p>Scenario: Calls generateWithImages with a vision model and an image file, expecting a
     * non-empty response. Usage: generateWithImages, image from file, no streaming.
     */
    @Test
    @Order(18)
    void shouldGenerateWithImageFiles()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(VISION_MODEL);
        File imageFile = getImageFileFromClasspath("roses.jpg");
        try {
            OllamaResult result =
                    api.generateWithImages(
                            VISION_MODEL,
                            "What is in this image?",
                            List.of(imageFile),
                            new OptionsBuilder().build(),
                            null,
                            null);
            assertNotNull(result);
            assertNotNull(result.getResponse());
            assertFalse(result.getResponse().isEmpty());
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            fail(e);
        }
    }

    /**
     * Tests generateWithImages with image file input and streaming enabled.
     *
     * <p>Scenario: Calls generateWithImages with a vision model, an image file, and a streaming
     * handler for the response. Usage: generateWithImages, image from file, streaming enabled.
     */
    @Test
    @Order(20)
    void shouldGenerateWithImageFilesAndResponseStreamed()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(VISION_MODEL);

        File imageFile = getImageFileFromClasspath("roses.jpg");

        OllamaResult result =
                api.generateWithImages(
                        VISION_MODEL,
                        "What is in this image?",
                        List.of(imageFile),
                        new OptionsBuilder().build(),
                        null,
                        LOG::info);
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
    }

    /**
     * Tests generate with thinking enabled (no streaming).
     *
     * <p>Scenario: Calls generate with think=true, expecting both response and thinking fields to
     * be populated. Usage: generate, think=true, no streaming.
     */
    @Test
    @Order(20)
    void shouldGenerateWithThinking()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(THINKING_TOOL_MODEL);

        boolean raw = false;
        boolean think = true;

        OllamaResult result =
                api.generate(
                        THINKING_TOOL_MODEL,
                        "Who are you?",
                        raw,
                        think,
                        new OptionsBuilder().build(),
                        new OllamaGenerateStreamObserver(null, null));
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertNotNull(result.getThinking());
    }

    /**
     * Tests generate with thinking and streaming enabled.
     *
     * <p>Scenario: Calls generate with think=true and a stream handler for both thinking and
     * response tokens. Usage: generate, think=true, streaming enabled.
     */
    @Test
    @Order(20)
    void shouldGenerateWithThinkingAndStreamHandler()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(THINKING_TOOL_MODEL);
        boolean raw = false;
        OllamaResult result =
                api.generate(
                        THINKING_TOOL_MODEL,
                        "Who are you?",
                        raw,
                        true,
                        new OptionsBuilder().build(),
                        new OllamaGenerateStreamObserver(
                                thinkingToken -> {
                                    LOG.info(thinkingToken.toUpperCase());
                                },
                                resToken -> {
                                    LOG.info(resToken.toLowerCase());
                                }));
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertNotNull(result.getThinking());
    }

    /**
     * Tests generate with raw=true parameter.
     *
     * <p>Scenario: Calls generate with raw=true, which sends the prompt as-is without any
     * formatting. Usage: generate, raw=true, no thinking, no streaming.
     */
    @Test
    @Order(21)
    void shouldGenerateWithRawMode()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(GENERAL_PURPOSE_MODEL);
        boolean raw = true;
        boolean thinking = false;
        OllamaResult result =
                api.generate(
                        GENERAL_PURPOSE_MODEL,
                        "What is 2+2?",
                        raw,
                        thinking,
                        new OptionsBuilder().build(),
                        new OllamaGenerateStreamObserver(null, null));
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
    }

    /**
     * Tests generate with raw=true and streaming enabled.
     *
     * <p>Scenario: Calls generate with raw=true and streams the response. Usage: generate,
     * raw=true, no thinking, streaming enabled.
     */
    @Test
    @Order(22)
    void shouldGenerateWithRawModeAndStreaming()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(GENERAL_PURPOSE_MODEL);
        boolean raw = true;
        OllamaResult result =
                api.generate(
                        GENERAL_PURPOSE_MODEL,
                        "What is the largest planet in our solar system?",
                        raw,
                        false,
                        new OptionsBuilder().build(),
                        new OllamaGenerateStreamObserver(
                                null, new ConsoleOutputGenerateTokenHandler()));

        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
    }

    /**
     * Tests generate with raw=true and thinking enabled.
     *
     * <p>Scenario: Calls generate with raw=true and think=true combination. Usage: generate,
     * raw=true, thinking enabled, no streaming.
     */
    @Test
    @Order(23)
    void shouldGenerateWithRawModeAndThinking()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(THINKING_TOOL_MODEL);
        boolean raw =
                true; // if true no formatting will be applied to the prompt. You may choose to use
        // the raw parameter if you are specifying a full templated prompt in your
        // request to the API
        boolean thinking = true;
        OllamaResult result =
                api.generate(
                        THINKING_TOOL_MODEL,
                        "What is a catalyst?",
                        raw,
                        thinking,
                        new OptionsBuilder().build(),
                        new OllamaGenerateStreamObserver(null, null));
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertNotNull(result.getThinking());
    }

    /**
     * Tests generate with all parameters enabled: raw=true, thinking=true, and streaming.
     *
     * <p>Scenario: Calls generate with all possible parameters enabled. Usage: generate, raw=true,
     * thinking enabled, streaming enabled.
     */
    @Test
    @Order(24)
    void shouldGenerateWithAllParametersEnabled()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(THINKING_TOOL_MODEL);
        // Settinng raw here instructs to keep the response raw. Even if the model generates
        // 'thinking' tokens, they will not be received as separate tokens and will be mised with
        // 'response' tokens
        boolean raw = true;
        OllamaResult result =
                api.generate(
                        THINKING_TOOL_MODEL,
                        "Count 1 to 5. Just give me the numbers and do not give any other details"
                                + " or information.",
                        raw,
                        true,
                        new OptionsBuilder().setTemperature(0.1f).build(),
                        new OllamaGenerateStreamObserver(
                                thinkingToken -> LOG.info("THINKING: {}", thinkingToken),
                                responseToken -> LOG.info("RESPONSE: {}", responseToken)));
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertNotNull(result.getThinking());
    }

    /**
     * Tests generateWithFormat with complex nested JSON schema.
     *
     * <p>Scenario: Uses a more complex JSON schema with nested objects and arrays. Usage:
     * generateWithFormat with complex schema.
     */
    @Test
    @Order(25)
    void shouldGenerateWithComplexStructuredOutput()
            throws OllamaBaseException, IOException, InterruptedException, URISyntaxException {
        api.pullModel(TOOLS_MODEL);

        String prompt =
                "Generate information about three major cities: their names, populations, and top"
                        + " attractions.";

        Map<String, Object> format = new HashMap<>();
        format.put("type", "object");
        Map<String, Object> properties = new HashMap<>();

        Map<String, Object> citiesProperty = new HashMap<>();
        citiesProperty.put("type", "array");

        Map<String, Object> cityItem = new HashMap<>();
        cityItem.put("type", "object");

        Map<String, Object> cityProperties = new HashMap<>();
        cityProperties.put("name", Map.of("type", "string"));
        cityProperties.put("population", Map.of("type", "number"));

        Map<String, Object> attractionsProperty = new HashMap<>();
        attractionsProperty.put("type", "array");
        attractionsProperty.put("items", Map.of("type", "string"));
        cityProperties.put("attractions", attractionsProperty);

        cityItem.put("properties", cityProperties);
        cityItem.put("required", List.of("name", "population", "attractions"));

        citiesProperty.put("items", cityItem);
        properties.put("cities", citiesProperty);

        format.put("properties", properties);
        format.put("required", List.of("cities"));

        OllamaResult result = api.generateWithFormat(TOOLS_MODEL, prompt, format);

        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertNotNull(result.getStructuredResponse());
        assertTrue(result.getStructuredResponse().containsKey("cities"));
    }

    /**
     * Tests chat with thinking enabled but no streaming.
     *
     * <p>Scenario: Enables thinking in chat mode without streaming. Usage: chat, thinking enabled,
     * no streaming, no tools.
     */
    @Test
    @Order(26)
    void shouldChatWithThinkingNoStream()
            throws OllamaBaseException,
                    IOException,
                    URISyntaxException,
                    InterruptedException,
                    ToolInvocationException {
        api.pullModel(THINKING_TOOL_MODEL);
        OllamaChatRequestBuilder builder =
                OllamaChatRequestBuilder.getInstance(THINKING_TOOL_MODEL);
        OllamaChatRequest requestModel =
                builder.withMessage(
                                OllamaChatMessageRole.USER,
                                "What is the meaning of life? Think deeply about this.")
                        .withThinking(true)
                        .build();

        OllamaChatResult chatResult = api.chat(requestModel, null);

        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertNotNull(chatResult.getResponseModel().getMessage());
        assertNotNull(chatResult.getResponseModel().getMessage().getResponse());
        // Note: Thinking content might be in the message or separate field depending on
        // implementation
    }

    /**
     * Tests chat with custom options and streaming.
     *
     * <p>Scenario: Combines custom options (temperature, top_p, etc.) with streaming. Usage: chat,
     * custom options, streaming enabled, no tools, no thinking.
     */
    @Test
    @Order(27)
    void shouldChatWithCustomOptionsAndStreaming()
            throws OllamaBaseException,
                    IOException,
                    URISyntaxException,
                    InterruptedException,
                    ToolInvocationException {
        api.pullModel(GENERAL_PURPOSE_MODEL);

        OllamaChatRequestBuilder builder =
                OllamaChatRequestBuilder.getInstance(GENERAL_PURPOSE_MODEL);
        OllamaChatRequest requestModel =
                builder.withMessage(
                                OllamaChatMessageRole.USER,
                                "Tell me a creative story about a time traveler")
                        .withOptions(
                                new OptionsBuilder()
                                        .setTemperature(0.9f)
                                        .setTopP(0.9f)
                                        .setTopK(40)
                                        .build())
                        .build();

        OllamaChatResult chatResult = api.chat(requestModel, new ConsoleOutputChatTokenHandler());

        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertNotNull(chatResult.getResponseModel().getMessage().getResponse());
        assertFalse(chatResult.getResponseModel().getMessage().getResponse().isEmpty());
    }

    /**
     * Tests chat with tools, thinking, and streaming all enabled.
     *
     * <p>Scenario: The most complex chat scenario with all features enabled. Usage: chat, tools,
     * thinking enabled, streaming enabled.
     */
    @Test
    @Order(28)
    void shouldChatWithToolsThinkingAndStreaming()
            throws OllamaBaseException,
                    IOException,
                    URISyntaxException,
                    InterruptedException,
                    ToolInvocationException {
        api.pullModel(THINKING_TOOL_MODEL_2);

        api.registerTool(employeeFinderTool());

        OllamaChatRequestBuilder builder =
                OllamaChatRequestBuilder.getInstance(THINKING_TOOL_MODEL_2);
        OllamaChatRequest requestModel =
                builder.withMessage(
                                OllamaChatMessageRole.USER,
                                "I need to find information about employee John Smith. Think"
                                        + " carefully about what details to retrieve.")
                        .withThinking(true)
                        .withOptions(new OptionsBuilder().setTemperature(0.1f).build())
                        .build();
        requestModel.setUseTools(false);
        OllamaChatResult chatResult = api.chat(requestModel, new ConsoleOutputChatTokenHandler());

        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        // Verify that either tools were called or a response was generated
        assertTrue(chatResult.getChatHistory().size() >= 2);
    }

    /**
     * Tests generateWithImages with multiple image URLs.
     *
     * <p>Scenario: Sends multiple image URLs to the vision model. Usage: generateWithImages,
     * multiple image URLs, no streaming.
     */
    @Test
    @Order(29)
    void shouldGenerateWithMultipleImageURLs()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(VISION_MODEL);

        List<Object> imageUrls =
                Arrays.asList(
                        "https://i.pinimg.com/736x/f9/4e/cb/f94ecba040696a3a20b484d2e15159ec.jpg",
                        "https://t3.ftcdn.net/jpg/02/96/63/80/360_F_296638053_0gUVA4WVBKceGsIr7LNqRWSnkusi07dq.jpg");

        OllamaResult result =
                api.generateWithImages(
                        VISION_MODEL,
                        "Compare these two images. What are the similarities and differences?",
                        imageUrls,
                        new OptionsBuilder().build(),
                        null,
                        null);

        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
    }

    /**
     * Tests generateWithImages with mixed image sources (URL and file).
     *
     * <p>Scenario: Combines image URL with local file in a single request. Usage:
     * generateWithImages, mixed image sources, no streaming.
     */
    @Test
    @Order(30)
    void shouldGenerateWithMixedImageSources()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(VISION_MODEL);

        File localImage = getImageFileFromClasspath("emoji-smile.jpeg");
        List<Object> images =
                Arrays.asList(
                        "https://i.pinimg.com/736x/f9/4e/cb/f94ecba040696a3a20b484d2e15159ec.jpg",
                        localImage);

        OllamaResult result =
                api.generateWithImages(
                        VISION_MODEL,
                        "Describe what you see in these images",
                        images,
                        new OptionsBuilder().build(),
                        null,
                        null);

        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
    }

    /**
     * Tests chat with multiple images in a single message.
     *
     * <p>Scenario: Sends multiple images in one chat message. Usage: chat, vision model, multiple
     * images, no tools, no thinking, no streaming.
     */
    @Test
    @Order(31)
    void shouldChatWithMultipleImages()
            throws OllamaBaseException,
                    IOException,
                    URISyntaxException,
                    InterruptedException,
                    ToolInvocationException {
        api.pullModel(VISION_MODEL);

        List<OllamaChatToolCalls> tools = Collections.emptyList();

        File image1 = getImageFileFromClasspath("emoji-smile.jpeg");
        File image2 = getImageFileFromClasspath("roses.jpg");

        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(VISION_MODEL);
        OllamaChatRequest requestModel =
                builder.withMessage(
                                OllamaChatMessageRole.USER,
                                "Compare these images and tell me what you see",
                                tools,
                                Arrays.asList(image1, image2))
                        .build();
        requestModel.setUseTools(false);
        OllamaChatResult chatResult = api.chat(requestModel, null);

        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertNotNull(chatResult.getResponseModel().getMessage().getResponse());
        assertFalse(chatResult.getResponseModel().getMessage().getResponse().isEmpty());
    }

    /**
     * Tests error handling when model doesn't exist.
     *
     * <p>Scenario: Attempts to use a non-existent model and verifies proper error handling.
     */
    @Test
    @Order(32)
    void shouldHandleNonExistentModel() {
        String nonExistentModel = "this-model-does-not-exist:latest";

        assertThrows(
                OllamaBaseException.class,
                () -> {
                    api.generate(
                            nonExistentModel,
                            "Hello",
                            false,
                            false,
                            new OptionsBuilder().build(),
                            new OllamaGenerateStreamObserver(null, null));
                });
    }

    /**
     * Tests chat with empty message (edge case).
     *
     * <p>Scenario: Sends an empty or whitespace-only message. Usage: chat, edge case testing.
     */
    @Test
    @Order(33)
    void shouldHandleEmptyMessage()
            throws OllamaBaseException,
                    IOException,
                    URISyntaxException,
                    InterruptedException,
                    ToolInvocationException {
        api.pullModel(GENERAL_PURPOSE_MODEL);

        List<OllamaChatToolCalls> tools = Collections.emptyList();
        OllamaChatRequestBuilder builder =
                OllamaChatRequestBuilder.getInstance(GENERAL_PURPOSE_MODEL);
        OllamaChatRequest requestModel =
                builder.withMessage(OllamaChatMessageRole.USER, "   ", tools) // whitespace only
                        .build();
        requestModel.setUseTools(false);
        OllamaChatResult chatResult = api.chat(requestModel, null);

        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        // Should handle gracefully even with empty input
    }

    /**
     * Tests generate with very high temperature setting.
     *
     * <p>Scenario: Tests extreme parameter values for robustness. Usage: generate, extreme
     * parameters, edge case testing.
     */
    @Test
    @Order(34)
    void shouldGenerateWithExtremeParameters()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(GENERAL_PURPOSE_MODEL);

        OllamaResult result =
                api.generate(
                        GENERAL_PURPOSE_MODEL,
                        "Generate a random word",
                        false,
                        false,
                        new OptionsBuilder()
                                .setTemperature(2.0f) // Very high temperature
                                .setTopP(1.0f)
                                .setTopK(1)
                                .build(),
                        new OllamaGenerateStreamObserver(null, null));

        assertNotNull(result);
        assertNotNull(result.getResponse());
    }

    /**
     * Tests embeddings with single input string.
     *
     * <p>Scenario: Tests embedding generation with a single string instead of array. Usage: embed,
     * single input.
     */
    @Test
    @Order(35)
    void shouldReturnEmbeddingsForSingleInput() throws Exception {
        api.pullModel(EMBEDDING_MODEL);

        OllamaEmbedRequestModel requestModel = new OllamaEmbedRequestModel();
        requestModel.setModel(EMBEDDING_MODEL);
        requestModel.setInput(
                Collections.singletonList("This is a single test sentence for embedding."));

        OllamaEmbedResponseModel embeddings = api.embed(requestModel);

        assertNotNull(embeddings);
        assertFalse(embeddings.getEmbeddings().isEmpty());
        assertEquals(1, embeddings.getEmbeddings().size());
    }

    /**
     * Tests chat with keep-alive parameter.
     *
     * <p>Scenario: Tests the keep-alive parameter which controls model unloading. Usage: chat,
     * keep-alive parameter, model lifecycle management.
     */
    @Test
    @Order(36)
    void shouldChatWithKeepAlive()
            throws OllamaBaseException,
                    IOException,
                    URISyntaxException,
                    InterruptedException,
                    ToolInvocationException {
        api.pullModel(GENERAL_PURPOSE_MODEL);

        OllamaChatRequestBuilder builder =
                OllamaChatRequestBuilder.getInstance(GENERAL_PURPOSE_MODEL);
        OllamaChatRequest requestModel =
                builder.withMessage(OllamaChatMessageRole.USER, "Hello, how are you?")
                        .withKeepAlive("5m") // Keep model loaded for 5 minutes
                        .build();
        requestModel.setUseTools(false);
        OllamaChatResult chatResult = api.chat(requestModel, null);

        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertNotNull(chatResult.getResponseModel().getMessage().getResponse());
    }

    /**
     * Tests generate with custom context window options.
     *
     * <p>Scenario: Tests generation with custom context length and other advanced options. Usage:
     * generate, advanced options, context management.
     */
    @Test
    @Order(37)
    void shouldGenerateWithAdvancedOptions()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(GENERAL_PURPOSE_MODEL);

        OllamaResult result =
                api.generate(
                        GENERAL_PURPOSE_MODEL,
                        "Write a detailed explanation of machine learning",
                        false,
                        false,
                        new OptionsBuilder()
                                .setTemperature(0.7f)
                                .setTopP(0.9f)
                                .setTopK(40)
                                .setNumCtx(4096) // Context window size
                                .setRepeatPenalty(1.1f)
                                .build(),
                        new OllamaGenerateStreamObserver(null, null));

        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
    }

    /**
     * Tests concurrent chat requests to verify thread safety.
     *
     * <p>Scenario: Sends multiple chat requests concurrently to test thread safety. Usage: chat,
     * concurrency testing, thread safety.
     */
    @Test
    @Order(38)
    void shouldHandleConcurrentChatRequests()
            throws InterruptedException, OllamaBaseException, IOException, URISyntaxException {
        api.pullModel(GENERAL_PURPOSE_MODEL);

        int numThreads = 3;
        CountDownLatch latch = new CountDownLatch(numThreads);
        List<OllamaChatResult> results = Collections.synchronizedList(new ArrayList<>());
        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            Thread thread =
                    new Thread(
                            () -> {
                                try {
                                    OllamaChatRequestBuilder builder =
                                            OllamaChatRequestBuilder.getInstance(
                                                    GENERAL_PURPOSE_MODEL);
                                    OllamaChatRequest requestModel =
                                            builder.withMessage(
                                                            OllamaChatMessageRole.USER,
                                                            "Hello from thread "
                                                                    + threadId
                                                                    + ". What is 2+2?")
                                                    .build();
                                    requestModel.setUseTools(false);
                                    OllamaChatResult result = api.chat(requestModel, null);
                                    results.add(result);
                                } catch (Exception e) {
                                    exceptions.add(e);
                                } finally {
                                    latch.countDown();
                                }
                            });
            thread.start();
        }

        latch.await(60, java.util.concurrent.TimeUnit.SECONDS);

        assertTrue(exceptions.isEmpty(), "No exceptions should occur during concurrent requests");
        assertEquals(numThreads, results.size(), "All requests should complete successfully");

        for (OllamaChatResult result : results) {
            assertNotNull(result);
            assertNotNull(result.getResponseModel());
            assertNotNull(result.getResponseModel().getMessage().getResponse());
        }
    }

    /**
     * Utility method to retrieve an image file from the classpath.
     *
     * <p>
     *
     * @param fileName the name of the image file
     * @return the File object for the image
     */
    private File getImageFileFromClasspath(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
    }

    /**
     * Returns a ToolSpecification for an employee finder tool.
     *
     * <p>This tool can be registered with the OllamaAPI to enable tool-calling scenarios in chat.
     * The tool accepts employee-name, employee-address, and employee-phone as parameters.
     */
    private Tools.ToolSpecification employeeFinderTool() {
        return Tools.ToolSpecification.builder()
                .functionName("get-employee-details")
                .functionDescription("Get details for a person or an employee")
                .toolPrompt(
                        Tools.PromptFuncDefinition.builder()
                                .type("function")
                                .function(
                                        Tools.PromptFuncDefinition.PromptFuncSpec.builder()
                                                .name("get-employee-details")
                                                .description(
                                                        "Get details for a person or an employee")
                                                .parameters(
                                                        Tools.PromptFuncDefinition.Parameters
                                                                .builder()
                                                                .type("object")
                                                                .properties(
                                                                        new Tools.PropsBuilder()
                                                                                .withProperty(
                                                                                        "employee-name",
                                                                                        Tools
                                                                                                .PromptFuncDefinition
                                                                                                .Property
                                                                                                .builder()
                                                                                                .type(
                                                                                                        "string")
                                                                                                .description(
                                                                                                        "The name"
                                                                                                            + " of the"
                                                                                                            + " employee,"
                                                                                                            + " e.g."
                                                                                                            + " John"
                                                                                                            + " Doe")
                                                                                                .required(
                                                                                                        true)
                                                                                                .build())
                                                                                .withProperty(
                                                                                        "employee-address",
                                                                                        Tools
                                                                                                .PromptFuncDefinition
                                                                                                .Property
                                                                                                .builder()
                                                                                                .type(
                                                                                                        "string")
                                                                                                .description(
                                                                                                        "The address"
                                                                                                            + " of the"
                                                                                                            + " employee,"
                                                                                                            + " Always"
                                                                                                            + " returns"
                                                                                                            + " a random"
                                                                                                            + " address."
                                                                                                            + " For example,"
                                                                                                            + " Church"
                                                                                                            + " St, Bengaluru,"
                                                                                                            + " India")
                                                                                                .required(
                                                                                                        true)
                                                                                                .build())
                                                                                .withProperty(
                                                                                        "employee-phone",
                                                                                        Tools
                                                                                                .PromptFuncDefinition
                                                                                                .Property
                                                                                                .builder()
                                                                                                .type(
                                                                                                        "string")
                                                                                                .description(
                                                                                                        "The phone"
                                                                                                            + " number"
                                                                                                            + " of the"
                                                                                                            + " employee."
                                                                                                            + " Always"
                                                                                                            + " returns"
                                                                                                            + " a random"
                                                                                                            + " phone"
                                                                                                            + " number."
                                                                                                            + " For example,"
                                                                                                            + " 9911002233")
                                                                                                .required(
                                                                                                        true)
                                                                                                .build())
                                                                                .build())
                                                                .required(List.of("employee-name"))
                                                                .build())
                                                .build())
                                .build())
                .toolFunction(
                        new ToolFunction() {
                            @Override
                            public Object apply(Map<String, Object> arguments) {
                                LOG.info(
                                        "Invoking employee finder tool with arguments: {}",
                                        arguments);
                                String employeeName = "Random Employee";
                                if (arguments.containsKey("employee-name")) {
                                    employeeName = arguments.get("employee-name").toString();
                                }
                                String address = null;
                                String phone = null;
                                if (employeeName.equalsIgnoreCase("Rahul Kumar")) {
                                    address = "Pune, Maharashtra, India";
                                    phone = "9911223344";
                                } else {
                                    address = "Karol Bagh, Delhi, India";
                                    phone = "9911002233";
                                }
                                // perform DB operations here
                                return String.format(
                                        "Employee Details {ID: %s, Name: %s, Address: %s, Phone:"
                                                + " %s}",
                                        UUID.randomUUID(), employeeName, address, phone);
                            }
                        })
                .build();
    }
}
