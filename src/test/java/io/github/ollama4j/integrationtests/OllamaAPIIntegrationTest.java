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
import io.github.ollama4j.models.chat.*;
import io.github.ollama4j.models.embeddings.OllamaEmbedRequestModel;
import io.github.ollama4j.models.embeddings.OllamaEmbedResponseModel;
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
@SuppressWarnings({"HttpUrlsUsage", "SpellCheckingInspection"})
class OllamaAPIIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(OllamaAPIIntegrationTest.class);

    private static OllamaContainer ollama;
    private static OllamaAPI api;

    private static final String EMBEDDING_MODEL = "all-minilm";
    private static final String VISION_MODEL = "moondream:1.8b";
    private static final String THINKING_TOOL_MODEL = "gpt-oss:20b";
    private static final String GENERAL_PURPOSE_MODEL = "gemma3:270m";
    private static final String TOOLS_MODEL = "mistral:7b";

    @BeforeAll
    static void setUp() {
        try {
            boolean useExternalOllamaHost =
                    Boolean.parseBoolean(System.getenv("USE_EXTERNAL_OLLAMA_HOST"));
            String ollamaHost = System.getenv("OLLAMA_HOST");

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
        api.setRequestTimeoutSeconds(120);
        api.setNumberOfRetriesForModelPull(5);
    }

    @Test
    @Order(1)
    void testWrongEndpoint() {
        OllamaAPI ollamaAPI = new OllamaAPI("http://wrong-host:11434");
        assertThrows(ConnectException.class, ollamaAPI::listModels);
    }

    @Test
    @Order(1)
    void testVersionAPI()
            throws URISyntaxException, IOException, OllamaBaseException, InterruptedException {
        // String expectedVersion = ollama.getDockerImageName().split(":")[1];
        String actualVersion = api.getVersion();
        assertNotNull(actualVersion);
        // assertEquals(expectedVersion, actualVersion, "Version should match the Docker
        // image version");
    }

    @Test
    @Order(1)
    void testPing() throws OllamaBaseException {
        boolean pingResponse = api.ping();
        assertTrue(pingResponse, "Ping should return true");
    }

    @Test
    @Order(2)
    void testListModelsAPI()
            throws URISyntaxException, IOException, OllamaBaseException, InterruptedException {
        // Fetch the list of models
        List<Model> models = api.listModels();
        // Assert that the models list is not null
        assertNotNull(models, "Models should not be null");
        // Assert that models list is either empty or contains more than 0 models
        assertTrue(models.size() >= 0, "Models list should not be empty");
    }

    @Test
    @Order(3)
    void testPullModelAPI()
            throws URISyntaxException, IOException, OllamaBaseException, InterruptedException {
        api.pullModel(EMBEDDING_MODEL);
        List<Model> models = api.listModels();
        assertNotNull(models, "Models should not be null");
        assertFalse(models.isEmpty(), "Models list should contain elements");
    }

    @Test
    @Order(4)
    void testListModelDetails()
            throws IOException, OllamaBaseException, URISyntaxException, InterruptedException {
        api.pullModel(EMBEDDING_MODEL);
        ModelDetail modelDetails = api.getModelDetails(EMBEDDING_MODEL);
        assertNotNull(modelDetails);
        assertTrue(modelDetails.getModelFile().contains(EMBEDDING_MODEL));
    }

    @Test
    @Order(5)
    void testEmbeddings() throws Exception {
        api.pullModel(EMBEDDING_MODEL);
        OllamaEmbedRequestModel m = new OllamaEmbedRequestModel();
        m.setModel(EMBEDDING_MODEL);
        m.setInput(Arrays.asList("Why is the sky blue?", "Why is the grass green?"));
        OllamaEmbedResponseModel embeddings = api.embed(m);
        assertNotNull(embeddings, "Embeddings should not be null");
        assertFalse(embeddings.getEmbeddings().isEmpty(), "Embeddings should not be empty");
    }

    @Test
    @Order(6)
    void testGenerateWithStructuredOutput()
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

        OllamaResult result = api.generate(TOOLS_MODEL, prompt, format);

        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());

        assertEquals(true, result.getStructuredResponse().get("isNoon"));
    }

    @Test
    @Order(6)
    void testGennerateModelWithDefaultOptions()
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
                        new OptionsBuilder().build());
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
    }

    @Test
    @Order(7)
    void testGenerateWithDefaultOptionsStreamed()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(GENERAL_PURPOSE_MODEL);
        boolean raw = false;
        StringBuffer sb = new StringBuffer();
        OllamaResult result =
                api.generate(
                        GENERAL_PURPOSE_MODEL,
                        "What is the capital of France? And what's France's connection with Mona"
                                + " Lisa?",
                        raw,
                        new OptionsBuilder().build(),
                        (s) -> {
                            LOG.info(s);
                            sb.append(s);
                        });

        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
        assertEquals(sb.toString(), result.getResponse());
    }

    @Test
    @Order(8)
    void testGenerateWithOptions()
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
        assertFalse(chatResult.getResponseModel().getMessage().getContent().isEmpty());
    }

    @Test
    @Order(9)
    void testChatWithSystemPrompt()
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
        assertFalse(chatResult.getResponseModel().getMessage().getContent().isBlank());
        assertTrue(
                chatResult.getResponseModel().getMessage().getContent().contains(expectedResponse));
        assertEquals(3, chatResult.getChatHistory().size());
    }

    @Test
    @Order(10)
    void testChat() throws Exception {
        api.pullModel(THINKING_TOOL_MODEL);
        OllamaChatRequestBuilder builder =
                OllamaChatRequestBuilder.getInstance(THINKING_TOOL_MODEL);

        // Create the initial user question
        OllamaChatRequest requestModel =
                builder.withMessage(
                                OllamaChatMessageRole.USER, "What is 1+1? Answer only in numbers.")
                        .build();

        // Start conversation with model
        OllamaChatResult chatResult = api.chat(requestModel, null);

        assertTrue(
                chatResult.getChatHistory().stream()
                        .anyMatch(chat -> chat.getContent().contains("2")),
                "Expected chat history to contain '2'");

        requestModel =
                builder.withMessages(chatResult.getChatHistory())
                        .withMessage(OllamaChatMessageRole.USER, "And what is its squared value?")
                        .build();

        // Continue conversation with model
        chatResult = api.chat(requestModel, null);

        assertTrue(
                chatResult.getChatHistory().stream()
                        .anyMatch(chat -> chat.getContent().contains("4")),
                "Expected chat history to contain '4'");

        // Create the next user question: the third question
        requestModel =
                builder.withMessages(chatResult.getChatHistory())
                        .withMessage(
                                OllamaChatMessageRole.USER,
                                "What is the largest value between 2, 4 and 6?")
                        .build();

        // Continue conversation with the model for the third question
        chatResult = api.chat(requestModel, null);

        // verify the result
        assertNotNull(chatResult, "Chat result should not be null");
        assertTrue(
                chatResult.getChatHistory().size() > 2,
                "Chat history should contain more than two messages");
        assertTrue(
                chatResult
                        .getChatHistory()
                        .get(chatResult.getChatHistory().size() - 1)
                        .getContent()
                        .contains("6"),
                "Response should contain '6'");
    }

    @Test
    @Order(11)
    void testChatWithExplicitToolDefinition()
            throws OllamaBaseException,
                    IOException,
                    URISyntaxException,
                    InterruptedException,
                    ToolInvocationException {
        // Ensure default behavior (library handles tools) for baseline assertions
        api.setClientHandlesTools(false);
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

    @Test
    @Order(13)
    void testChatWithExplicitToolDefinitionWithClientHandlesTools()
            throws OllamaBaseException,
                    IOException,
                    URISyntaxException,
                    InterruptedException,
                    ToolInvocationException {
        String theToolModel = TOOLS_MODEL;
        api.pullModel(theToolModel);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(theToolModel);

        api.registerTool(employeeFinderTool());

        try {
            // enable client-handled tools so the library does not auto-execute tool calls
            api.setClientHandlesTools(true);

            OllamaChatRequest requestModel =
                    builder.withMessage(
                                    OllamaChatMessageRole.USER,
                                    "Give me the ID and address of the employee Rahul Kumar.")
                            .build();
            requestModel.setOptions(
                    new OptionsBuilder().setTemperature(0.9f).build().getOptionsMap());

            OllamaChatResult chatResult = api.chat(requestModel, null);

            assertNotNull(chatResult, "chatResult should not be null");
            assertNotNull(chatResult.getResponseModel(), "Response model should not be null");
            assertNotNull(
                    chatResult.getResponseModel().getMessage(),
                    "Response message should not be null");
            assertEquals(
                    OllamaChatMessageRole.ASSISTANT.getRoleName(),
                    chatResult.getResponseModel().getMessage().getRole().getRoleName(),
                    "Role of the response message should be ASSISTANT");

            // When clientHandlesTools is true, the assistant message should contain tool calls
            List<OllamaChatToolCalls> toolCalls =
                    chatResult.getResponseModel().getMessage().getToolCalls();
            assertNotNull(
                    toolCalls,
                    "Assistant message should contain tool calls when clientHandlesTools is true");
            assertFalse(toolCalls.isEmpty(), "Tool calls should not be empty");
            OllamaToolCallsFunction function = toolCalls.get(0).getFunction();
            assertEquals(
                    "get-employee-details",
                    function.getName(),
                    "Tool function name should be 'get-employee-details'");
            Object employeeName = function.getArguments().get("employee-name");
            assertNotNull(employeeName, "Employee name argument should not be null");
            assertEquals(
                    "Rahul Kumar", employeeName, "Employee name argument should be 'Rahul Kumar'");

            // Since tools were not auto-executed, chat history should contain only the user and
            // assistant messages
            assertEquals(
                    2,
                    chatResult.getChatHistory().size(),
                    "Chat history should contain only user and assistant (tool call) messages when"
                            + " clientHandlesTools is true");
        } finally {
            // reset to default to avoid affecting other tests
            api.setClientHandlesTools(false);
        }
    }

    @Test
    @Order(14)
    void testChatWithToolsAndStream()
            throws OllamaBaseException,
                    IOException,
                    URISyntaxException,
                    InterruptedException,
                    ToolInvocationException {
        // Ensure default behavior (library handles tools) for streamed test
        api.setClientHandlesTools(false);
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

        OllamaChatResult chatResult =
                api.chat(
                        requestModel,
                        new OllamaChatStreamObserver(
                                (s) -> {
                                    LOG.info(s.toUpperCase());
                                },
                                (s) -> {
                                    LOG.info(s.toLowerCase());
                                }));

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

    @Test
    @Order(12)
    void testChatWithAnnotatedToolsAndSingleParam()
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
        assertEquals(1, toolCalls.size());
        OllamaToolCallsFunction function = toolCalls.get(0).getFunction();
        assertEquals("computeImportantConstant", function.getName());
        assertEquals(1, function.getArguments().size());
        Object noOfDigits = function.getArguments().get("noOfDigits");
        assertNotNull(noOfDigits);
        assertEquals("5", noOfDigits.toString());
        assertTrue(chatResult.getChatHistory().size() > 2);
        List<OllamaChatToolCalls> finalToolCalls =
                chatResult.getResponseModel().getMessage().getToolCalls();
        assertNull(finalToolCalls);
    }

    @Test
    @Order(13)
    void testChatWithAnnotatedToolsAndMultipleParams()
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
        List<OllamaChatToolCalls> toolCalls = chatResult.getChatHistory().get(1).getToolCalls();
        assertEquals(1, toolCalls.size());
        OllamaToolCallsFunction function = toolCalls.get(0).getFunction();
        assertEquals("sayHello", function.getName());
        assertEquals(2, function.getArguments().size());
        Object name = function.getArguments().get("name");
        assertNotNull(name);
        assertEquals("Rahul", name);
        Object numberOfHearts = function.getArguments().get("numberOfHearts");
        assertNotNull(numberOfHearts);
        assertTrue(Integer.parseInt(numberOfHearts.toString()) > 1);
        assertTrue(chatResult.getChatHistory().size() > 2);
        List<OllamaChatToolCalls> finalToolCalls =
                chatResult.getResponseModel().getMessage().getToolCalls();
        assertNull(finalToolCalls);
    }

    @Test
    @Order(15)
    void testChatWithStream()
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
        StringBuffer sb = new StringBuffer();

        OllamaChatResult chatResult =
                api.chat(
                        requestModel,
                        new OllamaChatStreamObserver(
                                (s) -> {
                                    LOG.info(s.toUpperCase());
                                },
                                (s) -> {
                                    LOG.info(s.toLowerCase());
                                }));
        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertNotNull(chatResult.getResponseModel().getMessage());
        assertNotNull(chatResult.getResponseModel().getMessage().getContent());
        assertEquals(sb.toString(), chatResult.getResponseModel().getMessage().getContent());
    }

    @Test
    @Order(15)
    void testChatWithThinkingAndStream()
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
                                "What is the capital of France? And what's France's connection with"
                                        + " Mona Lisa?")
                        .withThinking(true)
                        .withKeepAlive("0m")
                        .build();
        StringBuffer sb = new StringBuffer();

        OllamaChatResult chatResult =
                api.chat(
                        requestModel,
                        new OllamaChatStreamObserver(
                                (s) -> {
                                    LOG.info(s.toUpperCase());
                                },
                                (s) -> {
                                    LOG.info(s.toLowerCase());
                                }));

        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertNotNull(chatResult.getResponseModel().getMessage());
        assertNotNull(chatResult.getResponseModel().getMessage().getContent());
        assertEquals(
                sb.toString(),
                chatResult.getResponseModel().getMessage().getThinking()
                        + chatResult.getResponseModel().getMessage().getContent());
    }

    @Test
    @Order(10)
    void testChatWithImageFromURL()
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

    @Test
    @Order(10)
    void testChatWithImageFromFileWithHistoryRecognition()
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

    @Test
    @Order(17)
    void testGenerateWithOptionsAndImageURLs()
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

    @Test
    @Order(18)
    void testGenerateWithOptionsAndImageFiles()
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

    @Test
    @Order(20)
    void testGenerateWithOptionsAndImageFilesStreamed()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(VISION_MODEL);

        File imageFile = getImageFileFromClasspath("roses.jpg");

        StringBuffer sb = new StringBuffer();

        OllamaResult result =
                api.generateWithImages(
                        VISION_MODEL,
                        "What is in this image?",
                        List.of(imageFile),
                        new OptionsBuilder().build(),
                        null,
                        (s) -> {
                            LOG.info(s);
                            sb.append(s);
                        });
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
        assertEquals(sb.toString(), result.getResponse());
    }

    @Test
    @Order(20)
    void testGenerateWithThinking()
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
                        new OptionsBuilder().build());
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
        assertNotNull(result.getThinking());
        assertFalse(result.getThinking().isEmpty());
    }

    @Test
    @Order(20)
    void testGenerateWithThinkingAndStreamHandler()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(THINKING_TOOL_MODEL);

        boolean raw = false;

        StringBuffer sb = new StringBuffer();
        OllamaResult result =
                api.generate(
                        THINKING_TOOL_MODEL,
                        "Who are you?",
                        raw,
                        new OptionsBuilder().build(),
                        (thinkingToken) -> {
                            sb.append(thinkingToken);
                            LOG.info(thinkingToken);
                        },
                        (resToken) -> {
                            sb.append(resToken);
                            LOG.info(resToken);
                        });
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
        assertNotNull(result.getThinking());
        assertFalse(result.getThinking().isEmpty());
        assertEquals(sb.toString(), result.getThinking() + result.getResponse());
    }

    private File getImageFileFromClasspath(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
    }

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
                                                                                                            + " eturns"
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
                                String employeeName = arguments.get("employee-name").toString();
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
