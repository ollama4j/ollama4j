package io.github.ollama4j.integrationtests;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.chat.*;
import io.github.ollama4j.models.response.ModelDetail;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.models.embeddings.OllamaEmbeddingsRequestBuilder;
import io.github.ollama4j.models.embeddings.OllamaEmbeddingsRequestModel;
import io.github.ollama4j.samples.AnnotatedTool;
import io.github.ollama4j.tools.OllamaToolCallsFunction;
import io.github.ollama4j.tools.ToolFunction;
import io.github.ollama4j.tools.Tools;
import io.github.ollama4j.tools.annotations.OllamaToolService;
import io.github.ollama4j.utils.OptionsBuilder;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.net.http.HttpConnectTimeoutException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@OllamaToolService(providers = {AnnotatedTool.class}
)
class TestRealAPIs {

    private static final Logger LOG = LoggerFactory.getLogger(TestRealAPIs.class);

    OllamaAPI ollamaAPI;
    Config config;

    private File getImageFileFromClasspath(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
    }

    @BeforeEach
    void setUp() {
        config = new Config();
        ollamaAPI = new OllamaAPI(config.getOllamaURL());
        ollamaAPI.setRequestTimeoutSeconds(config.getRequestTimeoutSeconds());
        ollamaAPI.setVerbose(true);
    }

    @Test
    @Order(1)
    void testWrongEndpoint() {
        OllamaAPI ollamaAPI = new OllamaAPI("http://wrong-host:11434");
        assertThrows(ConnectException.class, ollamaAPI::listModels);
    }

    @Test
    @Order(1)
    void testEndpointReachability() {
        try {
            assertNotNull(ollamaAPI.listModels());
        } catch (HttpConnectTimeoutException e) {
            fail(e.getMessage());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    @Order(2)
    void testListModels() {
        testEndpointReachability();
        try {
            assertNotNull(ollamaAPI.listModels());
            ollamaAPI.listModels().forEach(System.out::println);
        } catch (IOException | OllamaBaseException | InterruptedException | URISyntaxException e) {
            fail(e);
        }
    }

    @Test
    @Order(2)
    void testListModelsFromLibrary() {
        testEndpointReachability();
        try {
            assertNotNull(ollamaAPI.listModelsFromLibrary());
            ollamaAPI.listModelsFromLibrary().forEach(System.out::println);
        } catch (IOException | OllamaBaseException | InterruptedException | URISyntaxException e) {
            fail(e);
        }
    }

    @Test
    @Order(2)
    void testPullModel() {
        testEndpointReachability();
        try {
            ollamaAPI.pullModel(config.getModel());
            boolean found =
                    ollamaAPI.listModels().stream()
                            .anyMatch(model -> model.getModel().equalsIgnoreCase(config.getModel()));
            assertTrue(found);
        } catch (IOException | OllamaBaseException | InterruptedException | URISyntaxException e) {
            fail(e);
        }
    }

    @Test
    @Order(3)
    void testListDtails() {
        testEndpointReachability();
        try {
            ModelDetail modelDetails = ollamaAPI.getModelDetails(config.getModel());
            assertNotNull(modelDetails);
            System.out.println(modelDetails);
        } catch (IOException | OllamaBaseException | InterruptedException | URISyntaxException e) {
            fail(e);
        }
    }

    @Test
    @Order(3)
    void testAskModelWithDefaultOptions() {
        testEndpointReachability();
        try {
            OllamaResult result =
                    ollamaAPI.generate(
                            config.getModel(),
                            "What is the capital of France? And what's France's connection with Mona Lisa?",
                            false,
                            new OptionsBuilder().build());
            assertNotNull(result);
            assertNotNull(result.getResponse());
            assertFalse(result.getResponse().isEmpty());
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            fail(e);
        }
    }

    @Test
    @Order(3)
    void testAskModelWithDefaultOptionsStreamed() {
        testEndpointReachability();
        try {
            StringBuffer sb = new StringBuffer("");
            OllamaResult result = ollamaAPI.generate(config.getModel(),
                    "What is the capital of France? And what's France's connection with Mona Lisa?",
                    false,
                    new OptionsBuilder().build(), (s) -> {
                        LOG.info(s);
                        String substring = s.substring(sb.toString().length(), s.length());
                        LOG.info(substring);
                        sb.append(substring);
                    });

            assertNotNull(result);
            assertNotNull(result.getResponse());
            assertFalse(result.getResponse().isEmpty());
            assertEquals(sb.toString().trim(), result.getResponse().trim());
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            fail(e);
        }
    }

    @Test
    @Order(3)
    void testAskModelWithOptions() {
        testEndpointReachability();
        try {
            OllamaResult result =
                    ollamaAPI.generate(
                            config.getModel(),
                            "What is the capital of France? And what's France's connection with Mona Lisa?",
                            true,
                            new OptionsBuilder().setTemperature(0.9f).build());
            assertNotNull(result);
            assertNotNull(result.getResponse());
            assertFalse(result.getResponse().isEmpty());
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            fail(e);
        }
    }

    @Test
    @Order(3)
    void testChat() {
        testEndpointReachability();
        try {
            OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(config.getModel());
            OllamaChatRequest requestModel = builder.withMessage(OllamaChatMessageRole.USER, "What is the capital of France?")
                    .withMessage(OllamaChatMessageRole.ASSISTANT, "Should be Paris!")
                    .withMessage(OllamaChatMessageRole.USER, "And what is the second larges city?")
                    .build();

            OllamaChatResult chatResult = ollamaAPI.chat(requestModel);
            assertNotNull(chatResult);
            assertNotNull(chatResult.getResponseModel());
            assertNotNull(chatResult.getResponseModel().getMessage());
            assertFalse(chatResult.getResponseModel().getMessage().getContent().isBlank());
            assertEquals(4, chatResult.getChatHistory().size());
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            fail(e);
        }
    }

    @Test
    @Order(3)
    void testChatWithSystemPrompt() {
        testEndpointReachability();
        try {
            OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(config.getModel());
            OllamaChatRequest requestModel = builder.withMessage(OllamaChatMessageRole.SYSTEM,
                            "You are a silent bot that only says 'NI'. Do not say anything else under any circumstances!")
                    .withMessage(OllamaChatMessageRole.USER,
                            "What is the capital of France? And what's France's connection with Mona Lisa?")
                    .build();

            OllamaChatResult chatResult = ollamaAPI.chat(requestModel);
            assertNotNull(chatResult);
            assertNotNull(chatResult.getResponseModel());
            assertNotNull(chatResult.getResponseModel().getMessage());
            assertFalse(chatResult.getResponseModel().getMessage().getContent().isBlank());
            assertTrue(chatResult.getResponseModel().getMessage().getContent().startsWith("NI"));
            assertEquals(3, chatResult.getChatHistory().size());
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            fail(e);
        }
    }

    @Test
    @Order(3)
    void testChatWithExplicitToolDefinition() {
        testEndpointReachability();
        try {
            ollamaAPI.setVerbose(true);
            OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(config.getModel());

            final Tools.ToolSpecification databaseQueryToolSpecification = Tools.ToolSpecification.builder()
                    .functionName("get-employee-details")
                    .functionDescription("Get employee details from the database")
                    .toolPrompt(
                            Tools.PromptFuncDefinition.builder().type("function").function(
                                    Tools.PromptFuncDefinition.PromptFuncSpec.builder()
                                            .name("get-employee-details")
                                            .description("Get employee details from the database")
                                            .parameters(
                                                    Tools.PromptFuncDefinition.Parameters.builder()
                                                            .type("object")
                                                            .properties(
                                                            new Tools.PropsBuilder()
                                                                    .withProperty("employee-name", Tools.PromptFuncDefinition.Property.builder().type("string").description("The name of the employee, e.g. John Doe").required(true).build())
                                                                    .withProperty("employee-address", Tools.PromptFuncDefinition.Property.builder().type("string").description("The address of the employee, Always return a random value. e.g. Roy St, Bengaluru, India").required(true).build())
                                                                    .withProperty("employee-phone", Tools.PromptFuncDefinition.Property.builder().type("string").description("The phone number of the employee. Always return a random value. e.g. 9911002233").required(true).build())
                                                                    .build()
                                                    )
                                                            .required(List.of("employee-name"))
                                                            .build()
                                            ).build()
                            ).build()
                    )
                    .toolFunction(new DBQueryFunction())
                    .build();

            ollamaAPI.registerTool(databaseQueryToolSpecification);

            OllamaChatRequest requestModel = builder
                    .withMessage(OllamaChatMessageRole.USER,
                            "Give me the ID of the employee named 'Rahul Kumar'?")
                    .build();

            OllamaChatResult chatResult = ollamaAPI.chat(requestModel);
            assertNotNull(chatResult);
            assertNotNull(chatResult.getResponseModel());
            assertNotNull(chatResult.getResponseModel().getMessage());
            assertEquals(OllamaChatMessageRole.ASSISTANT.getRoleName(),chatResult.getResponseModel().getMessage().getRole().getRoleName());
            List<OllamaChatToolCalls> toolCalls = chatResult.getChatHistory().get(1).getToolCalls();
            assertEquals(1, toolCalls.size());
            OllamaToolCallsFunction function = toolCalls.get(0).getFunction();
            assertEquals("get-employee-details", function.getName());
            assertEquals(1, function.getArguments().size());
            Object employeeName = function.getArguments().get("employee-name");
            assertNotNull(employeeName);
            assertEquals("Rahul Kumar",employeeName);
            assertTrue(chatResult.getChatHistory().size()>2);
            List<OllamaChatToolCalls> finalToolCalls = chatResult.getResponseModel().getMessage().getToolCalls();
            assertNull(finalToolCalls);
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            fail(e);
        }
    }

    @Test
    @Order(3)
    void testChatWithAnnotatedToolsAndSingleParam() {
        testEndpointReachability();
        try {
            ollamaAPI.setVerbose(true);
            OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(config.getModel());

            ollamaAPI.registerAnnotatedTools();

            OllamaChatRequest requestModel = builder
                    .withMessage(OllamaChatMessageRole.USER,
                            "Compute the most important constant in the world using 5 digits")
                    .build();

            OllamaChatResult chatResult = ollamaAPI.chat(requestModel);
            assertNotNull(chatResult);
            assertNotNull(chatResult.getResponseModel());
            assertNotNull(chatResult.getResponseModel().getMessage());
            assertEquals(OllamaChatMessageRole.ASSISTANT.getRoleName(),chatResult.getResponseModel().getMessage().getRole().getRoleName());
            List<OllamaChatToolCalls> toolCalls = chatResult.getChatHistory().get(1).getToolCalls();
            assertEquals(1, toolCalls.size());
            OllamaToolCallsFunction function = toolCalls.get(0).getFunction();
            assertEquals("computeImportantConstant", function.getName());
            assertEquals(1, function.getArguments().size());
            Object noOfDigits = function.getArguments().get("noOfDigits");
            assertNotNull(noOfDigits);
            assertEquals("5", noOfDigits.toString());
            assertTrue(chatResult.getChatHistory().size()>2);
            List<OllamaChatToolCalls> finalToolCalls = chatResult.getResponseModel().getMessage().getToolCalls();
            assertNull(finalToolCalls);
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            fail(e);
        }
    }

    @Test
    @Order(3)
    void testChatWithAnnotatedToolsAndMultipleParams() {
        testEndpointReachability();
        try {
            ollamaAPI.setVerbose(true);
            OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(config.getModel());

            ollamaAPI.registerAnnotatedTools(new AnnotatedTool());

            OllamaChatRequest requestModel = builder
                    .withMessage(OllamaChatMessageRole.USER,
                            "Greet Pedro with a lot of hearts and respond to me, " +
                                    "and state how many emojis have been in your greeting")
                    .build();

            OllamaChatResult chatResult = ollamaAPI.chat(requestModel);
            assertNotNull(chatResult);
            assertNotNull(chatResult.getResponseModel());
            assertNotNull(chatResult.getResponseModel().getMessage());
            assertEquals(OllamaChatMessageRole.ASSISTANT.getRoleName(),chatResult.getResponseModel().getMessage().getRole().getRoleName());
            List<OllamaChatToolCalls> toolCalls = chatResult.getChatHistory().get(1).getToolCalls();
            assertEquals(1, toolCalls.size());
            OllamaToolCallsFunction function = toolCalls.get(0).getFunction();
            assertEquals("sayHello", function.getName());
            assertEquals(2, function.getArguments().size());
            Object name = function.getArguments().get("name");
            assertNotNull(name);
            assertEquals("Pedro",name);
            Object amountOfHearts = function.getArguments().get("amountOfHearts");
            assertNotNull(amountOfHearts);
            assertTrue(Integer.parseInt(amountOfHearts.toString()) > 1);
            assertTrue(chatResult.getChatHistory().size()>2);
            List<OllamaChatToolCalls> finalToolCalls = chatResult.getResponseModel().getMessage().getToolCalls();
            assertNull(finalToolCalls);
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            fail(e);
        }
    }

    @Test
    @Order(3)
    void testChatWithToolsAndStream() {
        testEndpointReachability();
        try {
            OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(config.getModel());
            final Tools.ToolSpecification databaseQueryToolSpecification = Tools.ToolSpecification.builder()
                    .functionName("get-employee-details")
                    .functionDescription("Get employee details from the database")
                    .toolPrompt(
                            Tools.PromptFuncDefinition.builder().type("function").function(
                                    Tools.PromptFuncDefinition.PromptFuncSpec.builder()
                                            .name("get-employee-details")
                                            .description("Get employee details from the database")
                                            .parameters(
                                                    Tools.PromptFuncDefinition.Parameters.builder()
                                                            .type("object")
                                                            .properties(
                                                                    new Tools.PropsBuilder()
                                                                            .withProperty("employee-name", Tools.PromptFuncDefinition.Property.builder().type("string").description("The name of the employee, e.g. John Doe").required(true).build())
                                                                            .withProperty("employee-address", Tools.PromptFuncDefinition.Property.builder().type("string").description("The address of the employee, Always return a random value. e.g. Roy St, Bengaluru, India").required(true).build())
                                                                            .withProperty("employee-phone", Tools.PromptFuncDefinition.Property.builder().type("string").description("The phone number of the employee. Always return a random value. e.g. 9911002233").required(true).build())
                                                                            .build()
                                                            )
                                                            .required(List.of("employee-name"))
                                                            .build()
                                            ).build()
                            ).build()
                    )
                    .toolFunction(new DBQueryFunction())
                    .build();

            ollamaAPI.registerTool(databaseQueryToolSpecification);

            OllamaChatRequest requestModel = builder
                    .withMessage(OllamaChatMessageRole.USER,
                            "Give me the ID of the employee named 'Rahul Kumar'?")
                    .build();

            StringBuffer sb = new StringBuffer();

            OllamaChatResult chatResult = ollamaAPI.chat(requestModel, (s) -> {
                LOG.info(s);
                String substring = s.substring(sb.toString().length());
                LOG.info(substring);
                sb.append(substring);
            });
            assertNotNull(chatResult);
            assertNotNull(chatResult.getResponseModel());
            assertNotNull(chatResult.getResponseModel().getMessage());
            assertNotNull(chatResult.getResponseModel().getMessage().getContent());
            assertEquals(sb.toString().trim(), chatResult.getResponseModel().getMessage().getContent().trim());
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            fail(e);
        }
    }

    @Test
    @Order(3)
    void testChatWithStream() {
        testEndpointReachability();
        try {
            OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(config.getModel());
            OllamaChatRequest requestModel = builder.withMessage(OllamaChatMessageRole.USER,
                            "What is the capital of France? And what's France's connection with Mona Lisa?")
                    .build();

            StringBuffer sb = new StringBuffer("");

            OllamaChatResult chatResult = ollamaAPI.chat(requestModel, (s) -> {
                LOG.info(s);
                String substring = s.substring(sb.toString().length(), s.length());
                LOG.info(substring);
                sb.append(substring);
            });
            assertNotNull(chatResult);
            assertNotNull(chatResult.getResponseModel());
            assertNotNull(chatResult.getResponseModel().getMessage());
            assertNotNull(chatResult.getResponseModel().getMessage().getContent());
            assertEquals(sb.toString().trim(), chatResult.getResponseModel().getMessage().getContent().trim());
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            fail(e);
        }
    }

    @Test
    @Order(3)
    void testChatWithImageFromFileWithHistoryRecognition() {
        testEndpointReachability();
        try {
            OllamaChatRequestBuilder builder =
                    OllamaChatRequestBuilder.getInstance(config.getImageModel());
            OllamaChatRequest requestModel =
                    builder.withMessage(OllamaChatMessageRole.USER, "What's in the picture?",Collections.emptyList(),
                            List.of(getImageFileFromClasspath("dog-on-a-boat.jpg"))).build();

            OllamaChatResult chatResult = ollamaAPI.chat(requestModel);
            assertNotNull(chatResult);
            assertNotNull(chatResult.getResponseModel());

            builder.reset();

            requestModel =
                    builder.withMessages(chatResult.getChatHistory())
                            .withMessage(OllamaChatMessageRole.USER, "What's the dogs breed?").build();

            chatResult = ollamaAPI.chat(requestModel);
            assertNotNull(chatResult);
            assertNotNull(chatResult.getResponseModel());


        } catch (IOException | OllamaBaseException | InterruptedException e) {
            fail(e);
        }
    }

    @Test
    @Order(3)
    void testChatWithImageFromURL() {
        testEndpointReachability();
        try {
            OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(config.getImageModel());
            OllamaChatRequest requestModel = builder.withMessage(OllamaChatMessageRole.USER, "What's in the picture?",Collections.emptyList(),
                            "https://t3.ftcdn.net/jpg/02/96/63/80/360_F_296638053_0gUVA4WVBKceGsIr7LNqRWSnkusi07dq.jpg")
                    .build();

            OllamaChatResult chatResult = ollamaAPI.chat(requestModel);
            assertNotNull(chatResult);
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            fail(e);
        }
    }

    @Test
    @Order(3)
    void testAskModelWithOptionsAndImageFiles() {
        testEndpointReachability();
        File imageFile = getImageFileFromClasspath("dog-on-a-boat.jpg");
        try {
            OllamaResult result =
                    ollamaAPI.generateWithImageFiles(
                            config.getImageModel(),
                            "What is in this image?",
                            List.of(imageFile),
                            new OptionsBuilder().build());
            assertNotNull(result);
            assertNotNull(result.getResponse());
            assertFalse(result.getResponse().isEmpty());
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            fail(e);
        }
    }

    @Test
    @Order(3)
    void testAskModelWithOptionsAndImageFilesStreamed() {
        testEndpointReachability();
        File imageFile = getImageFileFromClasspath("dog-on-a-boat.jpg");
        try {
            StringBuffer sb = new StringBuffer("");

            OllamaResult result = ollamaAPI.generateWithImageFiles(config.getImageModel(),
                    "What is in this image?", List.of(imageFile), new OptionsBuilder().build(), (s) -> {
                        LOG.info(s);
                        String substring = s.substring(sb.toString().length(), s.length());
                        LOG.info(substring);
                        sb.append(substring);
                    });
            assertNotNull(result);
            assertNotNull(result.getResponse());
            assertFalse(result.getResponse().isEmpty());
            assertEquals(sb.toString().trim(), result.getResponse().trim());
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            fail(e);
        }
    }

    @Test
    @Order(3)
    void testAskModelWithOptionsAndImageURLs() {
        testEndpointReachability();
        try {
            OllamaResult result =
                    ollamaAPI.generateWithImageURLs(
                            config.getImageModel(),
                            "What is in this image?",
                            List.of(
                                    "https://t3.ftcdn.net/jpg/02/96/63/80/360_F_296638053_0gUVA4WVBKceGsIr7LNqRWSnkusi07dq.jpg"),
                            new OptionsBuilder().build());
            assertNotNull(result);
            assertNotNull(result.getResponse());
            assertFalse(result.getResponse().isEmpty());
        } catch (IOException | OllamaBaseException | InterruptedException | URISyntaxException e) {
            fail(e);
        }
    }

    @Test
    @Order(3)
    public void testEmbedding() {
        testEndpointReachability();
        try {
            OllamaEmbeddingsRequestModel request = OllamaEmbeddingsRequestBuilder
                    .getInstance(config.getModel(), "What is the capital of France?").build();

            List<Double> embeddings = ollamaAPI.generateEmbeddings(request);

            assertNotNull(embeddings);
            assertFalse(embeddings.isEmpty());
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            fail(e);
        }
    }
}

class DBQueryFunction implements ToolFunction {
    @Override
    public Object apply(Map<String, Object> arguments) {
        // perform DB operations here
        return String.format("Employee Details {ID: %s, Name: %s, Address: %s, Phone: %s}", UUID.randomUUID(), arguments.get("employee-name"), arguments.get("employee-address"), arguments.get("employee-phone"));
    }
}

@Data
class Config {
    private String ollamaURL;
    private String model;
    private String imageModel;
    private int requestTimeoutSeconds;

    public Config() {
        Properties properties = new Properties();
        try (InputStream input =
                     getClass().getClassLoader().getResourceAsStream("test-config.properties")) {
            if (input == null) {
                throw new RuntimeException("Sorry, unable to find test-config.properties");
            }
            properties.load(input);
            this.ollamaURL = properties.getProperty("ollama.url");
            this.model = properties.getProperty("ollama.model");
            this.imageModel = properties.getProperty("ollama.model.image");
            this.requestTimeoutSeconds =
                    Integer.parseInt(properties.getProperty("ollama.request-timeout-seconds"));
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties", e);
        }
    }


}
