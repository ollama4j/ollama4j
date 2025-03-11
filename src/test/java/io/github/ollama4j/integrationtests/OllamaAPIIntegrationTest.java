package io.github.ollama4j.integrationtests;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.chat.*;
import io.github.ollama4j.models.embeddings.OllamaEmbedResponseModel;
import io.github.ollama4j.models.response.LibraryModel;
import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.models.response.ModelDetail;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.samples.AnnotatedTool;
import io.github.ollama4j.tools.OllamaToolCallsFunction;
import io.github.ollama4j.tools.ToolFunction;
import io.github.ollama4j.tools.Tools;
import io.github.ollama4j.tools.annotations.OllamaToolService;
import io.github.ollama4j.utils.OptionsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.ollama.OllamaContainer;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@OllamaToolService(providers = {AnnotatedTool.class})
@TestMethodOrder(OrderAnnotation.class)

@SuppressWarnings("HttpUrlsUsage")
public class OllamaAPIIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(OllamaAPIIntegrationTest.class);

    private static OllamaContainer ollama;
    private static OllamaAPI api;

    @BeforeAll
    public static void setUp() {
        String version = "0.5.13";
        int internalPort = 11434;
        int mappedPort = 11435;
        ollama = new OllamaContainer("ollama/ollama:" + version);
        ollama.addExposedPort(internalPort);
        List<String> portBindings = new ArrayList<>();
        portBindings.add(mappedPort + ":" + internalPort);
        ollama.setPortBindings(portBindings);
        ollama.start();
        api = new OllamaAPI("http://" + ollama.getHost() + ":" + ollama.getMappedPort(internalPort));
        api.setRequestTimeoutSeconds(60);
        api.setVerbose(true);
    }

    @Test
    @Order(1)
    void testWrongEndpoint() {
        OllamaAPI ollamaAPI = new OllamaAPI("http://wrong-host:11434");
        assertThrows(ConnectException.class, ollamaAPI::listModels);
    }

    @Test
    @Order(1)
    public void testVersionAPI() throws URISyntaxException, IOException, OllamaBaseException, InterruptedException {
        String expectedVersion = ollama.getDockerImageName().split(":")[1];
        String actualVersion = api.getVersion();
        assertEquals(expectedVersion, actualVersion, "Version should match the Docker image version");
    }

    @Test
    @Order(2)
    public void testListModelsAPI() throws URISyntaxException, IOException, OllamaBaseException, InterruptedException {
        // Fetch the list of models
        List<Model> models = api.listModels();
        // Assert that the models list is not null
        assertNotNull(models, "Models should not be null");
        // Assert that models list is either empty or contains more than 0 models
        assertTrue(models.size() >= 0, "Models list should be empty or contain elements");
    }

    @Test
    @Order(2)
    void testListModelsFromLibrary() throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        List<LibraryModel> models = api.listModelsFromLibrary();
        assertNotNull(models);
        assertFalse(models.isEmpty());
    }

    @Test
    @Order(3)
    public void testPullModelAPI() throws URISyntaxException, IOException, OllamaBaseException, InterruptedException {
        api.pullModel("all-minilm");
        List<Model> models = api.listModels();
        assertNotNull(models, "Models should not be null");
        assertFalse(models.isEmpty(), "Models list should contain elements");
    }

    @Test
    @Order(4)
    void testListModelDetails() throws IOException, OllamaBaseException, URISyntaxException, InterruptedException {
        String embeddingModelMinilm = "all-minilm";
        api.pullModel(embeddingModelMinilm);
        ModelDetail modelDetails = api.getModelDetails("all-minilm");
        assertNotNull(modelDetails);
        assertTrue(modelDetails.getModelFile().contains(embeddingModelMinilm));
    }

    @Test
    @Order(5)
    public void testEmbeddings() throws Exception {
        String embeddingModelMinilm = "all-minilm";
        api.pullModel(embeddingModelMinilm);
        OllamaEmbedResponseModel embeddings = api.embed(embeddingModelMinilm, Arrays.asList("Why is the sky blue?", "Why is the grass green?"));
        assertNotNull(embeddings, "Embeddings should not be null");
        assertFalse(embeddings.getEmbeddings().isEmpty(), "Embeddings should not be empty");
    }

    @Test
    @Order(6)
    void testAskModelWithDefaultOptions() throws OllamaBaseException, IOException, InterruptedException, URISyntaxException {
        String chatModel = "qwen2.5:0.5b";
        api.pullModel(chatModel);
        OllamaResult result =
                api.generate(
                        chatModel,
                        "What is the capital of France? And what's France's connection with Mona Lisa?",
                        false,
                        new OptionsBuilder().build());
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
    }

    @Test
    @Order(7)
    void testAskModelWithDefaultOptionsStreamed() throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        String chatModel = "qwen2.5:0.5b";
        api.pullModel(chatModel);
        StringBuffer sb = new StringBuffer();
        OllamaResult result = api.generate(chatModel,
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
    }

    @Test
    @Order(8)
    void testAskModelWithOptions() throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        String chatModel = "qwen2.5:0.5b-instruct";
        api.pullModel(chatModel);

        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(chatModel);
        OllamaChatRequest requestModel = builder.withMessage(OllamaChatMessageRole.SYSTEM, "You are a helpful assistant who can generate random person's first and last names in the format [First name, Last name].")
                .build();
        requestModel = builder.withMessages(requestModel.getMessages())
                .withMessage(OllamaChatMessageRole.USER, "Give me a cool name")
                .withOptions(new OptionsBuilder().setTemperature(0.5f).build()).build();
        OllamaChatResult chatResult = api.chat(requestModel);

        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertFalse(chatResult.getResponseModel().getMessage().getContent().isEmpty());
    }

    @Test
    @Order(9)
    void testChatWithSystemPrompt() throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        String chatModel = "llama3.2:1b";
        api.pullModel(chatModel);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(chatModel);
        OllamaChatRequest requestModel = builder.withMessage(OllamaChatMessageRole.SYSTEM,
                        "You are a silent bot that only says 'Shush'. Do not say anything else under any circumstances!")
                .withMessage(OllamaChatMessageRole.USER,
                        "What's something that's brown and sticky?")
                .withOptions(new OptionsBuilder().setTemperature(0.8f).build())
                .build();

        OllamaChatResult chatResult = api.chat(requestModel);
        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertNotNull(chatResult.getResponseModel().getMessage());
        assertFalse(chatResult.getResponseModel().getMessage().getContent().isBlank());
        assertTrue(chatResult.getResponseModel().getMessage().getContent().contains("Shush"));
        assertEquals(3, chatResult.getChatHistory().size());
    }

    @Test
    @Order(10)
    public void testChat() throws Exception {
        String chatModel = "qwen2.5:0.5b";
        api.pullModel(chatModel);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(chatModel);

        // Create the initial user question
        OllamaChatRequest requestModel = builder.withMessage(OllamaChatMessageRole.USER, "What is the capital of France?")
                .build();

        // Start conversation with model
        OllamaChatResult chatResult = api.chat(requestModel);

        assertTrue(
                chatResult.getChatHistory().stream()
                        .anyMatch(chat -> chat.getContent().contains("Paris")),
                "Expected chat history to contain 'Paris'"
        );

        // Create the next user question: second largest city
        requestModel = builder.withMessages(chatResult.getChatHistory())
                .withMessage(OllamaChatMessageRole.USER, "And what is its official language?")
                .build();

        // Continue conversation with model
        chatResult = api.chat(requestModel);

        assertTrue(
                chatResult.getChatHistory().stream()
                        .anyMatch(chat -> chat.getContent().contains("French")),
                "Expected chat history to contain 'French'"
        );

        // Create the next user question: the third question
        requestModel = builder.withMessages(chatResult.getChatHistory())
                .withMessage(OllamaChatMessageRole.USER, "What is the largest river in France?")
                .build();

        // Continue conversation with the model for the third question
        chatResult = api.chat(requestModel);

        // verify the result
        assertNotNull(chatResult, "Chat result should not be null");
        assertTrue(chatResult.getChatHistory().size() > 2, "Chat history should contain more than two messages");
        assertTrue(chatResult.getChatHistory().get(chatResult.getChatHistory().size() - 1).getContent().contains("river"), "Response should be related to river");
    }


    @Test
    @Order(11)
    void testChatWithExplicitToolDefinition() throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        String chatModel = "llama3.2:1b";
        api.pullModel(chatModel);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(chatModel);

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
                .toolFunction(arguments -> {
                    // perform DB operations here
                    return String.format("Employee Details {ID: %s, Name: %s, Address: %s, Phone: %s}", UUID.randomUUID(), arguments.get("employee-name"), arguments.get("employee-address"), arguments.get("employee-phone"));
                })
                .build();

        api.registerTool(databaseQueryToolSpecification);

        OllamaChatRequest requestModel = builder
                .withMessage(OllamaChatMessageRole.USER,
                        "Give me the ID of the employee named 'Rahul Kumar'?")
                .build();

        OllamaChatResult chatResult = api.chat(requestModel);
        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertNotNull(chatResult.getResponseModel().getMessage());
        assertEquals(OllamaChatMessageRole.ASSISTANT.getRoleName(), chatResult.getResponseModel().getMessage().getRole().getRoleName());
        List<OllamaChatToolCalls> toolCalls = chatResult.getChatHistory().get(1).getToolCalls();
        assertEquals(1, toolCalls.size());
        OllamaToolCallsFunction function = toolCalls.get(0).getFunction();
        assertEquals("get-employee-details", function.getName());
        assertEquals(1, function.getArguments().size());
        Object employeeName = function.getArguments().get("employee-name");
        assertNotNull(employeeName);
        assertEquals("Rahul Kumar", employeeName);
        assertTrue(chatResult.getChatHistory().size() > 2);
        List<OllamaChatToolCalls> finalToolCalls = chatResult.getResponseModel().getMessage().getToolCalls();
        assertNull(finalToolCalls);
    }

    @Test
    @Order(12)
    void testChatWithAnnotatedToolsAndSingleParam() throws OllamaBaseException, IOException, InterruptedException, URISyntaxException {
        String chatModel = "llama3.2:1b";
        api.pullModel(chatModel);

        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(chatModel);

        api.registerAnnotatedTools();

        OllamaChatRequest requestModel = builder
                .withMessage(OllamaChatMessageRole.USER,
                        "Compute the most important constant in the world using 5 digits")
                .build();

        OllamaChatResult chatResult = api.chat(requestModel);
        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertNotNull(chatResult.getResponseModel().getMessage());
        assertEquals(OllamaChatMessageRole.ASSISTANT.getRoleName(), chatResult.getResponseModel().getMessage().getRole().getRoleName());
        List<OllamaChatToolCalls> toolCalls = chatResult.getChatHistory().get(1).getToolCalls();
        assertEquals(1, toolCalls.size());
        OllamaToolCallsFunction function = toolCalls.get(0).getFunction();
        assertEquals("computeImportantConstant", function.getName());
        assertEquals(1, function.getArguments().size());
        Object noOfDigits = function.getArguments().get("noOfDigits");
        assertNotNull(noOfDigits);
        assertEquals("5", noOfDigits.toString());
        assertTrue(chatResult.getChatHistory().size() > 2);
        List<OllamaChatToolCalls> finalToolCalls = chatResult.getResponseModel().getMessage().getToolCalls();
        assertNull(finalToolCalls);
    }

    @Test
    @Order(13)
    void testChatWithAnnotatedToolsAndMultipleParams() throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        String chatModel = "llama3.2:1b";
        api.pullModel(chatModel);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(chatModel);

        api.registerAnnotatedTools(new AnnotatedTool());

        OllamaChatRequest requestModel = builder
                .withMessage(OllamaChatMessageRole.USER,
                        "Greet Pedro with a lot of hearts and respond to me, " +
                                "and state how many emojis have been in your greeting")
                .build();

        OllamaChatResult chatResult = api.chat(requestModel);
        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertNotNull(chatResult.getResponseModel().getMessage());
        assertEquals(OllamaChatMessageRole.ASSISTANT.getRoleName(), chatResult.getResponseModel().getMessage().getRole().getRoleName());
        List<OllamaChatToolCalls> toolCalls = chatResult.getChatHistory().get(1).getToolCalls();
        assertEquals(1, toolCalls.size());
        OllamaToolCallsFunction function = toolCalls.get(0).getFunction();
        assertEquals("sayHello", function.getName());
        assertEquals(2, function.getArguments().size());
        Object name = function.getArguments().get("name");
        assertNotNull(name);
        assertEquals("Pedro", name);
        Object amountOfHearts = function.getArguments().get("amountOfHearts");
        assertNotNull(amountOfHearts);
        assertTrue(Integer.parseInt(amountOfHearts.toString()) > 1);
        assertTrue(chatResult.getChatHistory().size() > 2);
        List<OllamaChatToolCalls> finalToolCalls = chatResult.getResponseModel().getMessage().getToolCalls();
        assertNull(finalToolCalls);
    }

    @Test
    @Order(14)
    void testChatWithToolsAndStream() throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        String chatModel = "llama3.2:1b";
        api.pullModel(chatModel);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(chatModel);
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
                .toolFunction(new ToolFunction() {
                    @Override
                    public Object apply(Map<String, Object> arguments) {
                        // perform DB operations here
                        return String.format("Employee Details {ID: %s, Name: %s, Address: %s, Phone: %s}", UUID.randomUUID(), arguments.get("employee-name"), arguments.get("employee-address"), arguments.get("employee-phone"));
                    }
                })
                .build();

        api.registerTool(databaseQueryToolSpecification);

        OllamaChatRequest requestModel = builder
                .withMessage(OllamaChatMessageRole.USER,
                        "Give me the ID of the employee named 'Rahul Kumar'?")
                .build();

        StringBuffer sb = new StringBuffer();

        OllamaChatResult chatResult = api.chat(requestModel, (s) -> {
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
    }

    @Test
    @Order(15)
    void testChatWithStream() throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        String chatModel = "llama3.2:1b";
        api.pullModel(chatModel);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(chatModel);
        OllamaChatRequest requestModel = builder.withMessage(OllamaChatMessageRole.USER,
                        "What is the capital of France? And what's France's connection with Mona Lisa?")
                .build();

        StringBuffer sb = new StringBuffer();

        OllamaChatResult chatResult = api.chat(requestModel, (s) -> {
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
    }

    @Test
    @Order(16)
    void testChatWithImageFromURL() throws OllamaBaseException, IOException, InterruptedException, URISyntaxException {
        String imageModel = "llava";
        api.pullModel(imageModel);

        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(imageModel);
        OllamaChatRequest requestModel = builder.withMessage(OllamaChatMessageRole.USER, "What's in the picture?", Collections.emptyList(),
                        "https://t3.ftcdn.net/jpg/02/96/63/80/360_F_296638053_0gUVA4WVBKceGsIr7LNqRWSnkusi07dq.jpg")
                .build();

        OllamaChatResult chatResult = api.chat(requestModel);
        assertNotNull(chatResult);
    }

    @Test
    @Order(17)
    void testAskModelWithOptionsAndImageURLs() throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        String imageModel = "llava";
        api.pullModel(imageModel);

        OllamaResult result =
                api.generateWithImageURLs(
                        imageModel,
                        "What is in this image?",
                        List.of(
                                "https://t3.ftcdn.net/jpg/02/96/63/80/360_F_296638053_0gUVA4WVBKceGsIr7LNqRWSnkusi07dq.jpg"),
                        new OptionsBuilder().build());
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
    }

    @Test
    @Order(18)
    void testAskModelWithOptionsAndImageFiles() throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        String imageModel = "llava";
        api.pullModel(imageModel);
        File imageFile = getImageFileFromClasspath("dog-on-a-boat.jpg");
        try {
            OllamaResult result =
                    api.generateWithImageFiles(
                            imageModel,
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
    @Order(19)
    void testChatWithImageFromFileWithHistoryRecognition() throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        String imageModel = "llava";
        api.pullModel(imageModel);
        OllamaChatRequestBuilder builder =
                OllamaChatRequestBuilder.getInstance(imageModel);
        OllamaChatRequest requestModel =
                builder.withMessage(OllamaChatMessageRole.USER, "What's in the picture?", Collections.emptyList(),
                        List.of(getImageFileFromClasspath("dog-on-a-boat.jpg"))).build();

        OllamaChatResult chatResult = api.chat(requestModel);
        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());

        builder.reset();

        requestModel =
                builder.withMessages(chatResult.getChatHistory())
                        .withMessage(OllamaChatMessageRole.USER, "What's the dogs breed?").build();

        chatResult = api.chat(requestModel);
        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
    }

    @Test
    @Order(20)
    void testAskModelWithOptionsAndImageFilesStreamed() throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        String imageModel = "llava";
        api.pullModel(imageModel);

        File imageFile = getImageFileFromClasspath("dog-on-a-boat.jpg");

        StringBuffer sb = new StringBuffer();

        OllamaResult result = api.generateWithImageFiles(imageModel,
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
    }

    private File getImageFileFromClasspath(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
    }
}
//
//@Data
//class Config {
//    private String ollamaURL;
//    private String model;
//    private String imageModel;
//    private int requestTimeoutSeconds;
//
//    public Config() {
//        Properties properties = new Properties();
//        try (InputStream input =
//                     getClass().getClassLoader().getResourceAsStream("test-config.properties")) {
//            if (input == null) {
//                throw new RuntimeException("Sorry, unable to find test-config.properties");
//            }
//            properties.load(input);
//            this.ollamaURL = properties.getProperty("ollama.url");
//            this.model = properties.getProperty("ollama.model");
//            this.imageModel = properties.getProperty("ollama.model.image");
//            this.requestTimeoutSeconds =
//                    Integer.parseInt(properties.getProperty("ollama.request-timeout-seconds"));
//        } catch (IOException e) {
//            throw new RuntimeException("Error loading properties", e);
//        }
//    }
//}
