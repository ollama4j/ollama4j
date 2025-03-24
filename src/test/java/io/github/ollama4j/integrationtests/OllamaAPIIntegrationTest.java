package io.github.ollama4j.integrationtests;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

@SuppressWarnings({"HttpUrlsUsage", "SpellCheckingInspection"})
public class OllamaAPIIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(OllamaAPIIntegrationTest.class);

    private static OllamaContainer ollama;
    private static OllamaAPI api;

    private static final String EMBEDDING_MODEL_MINILM = "all-minilm";
    private static final String CHAT_MODEL_QWEN_SMALL = "qwen2.5:0.5b";
    private static final String CHAT_MODEL_INSTRUCT = "qwen2.5:0.5b-instruct";
    private static final String CHAT_MODEL_SYSTEM_PROMPT = "llama3.2:1b";
    private static final String CHAT_MODEL_LLAMA3 = "llama3";
    private static final String IMAGE_MODEL_LLAVA = "llava";

    @BeforeAll
    public static void setUp() {
        try {
            boolean useExternalOllamaHost = Boolean.parseBoolean(System.getenv("USE_EXTERNAL_OLLAMA_HOST"));
            String ollamaHost = System.getenv("OLLAMA_HOST");
            if (useExternalOllamaHost) {
                api = new OllamaAPI(ollamaHost);
            } else {
                throw new RuntimeException(
                        "USE_EXTERNAL_OLLAMA_HOST is not set so, we will be using Testcontainers Ollama host for the tests now. If you would like to use an external host, please set the env var to USE_EXTERNAL_OLLAMA_HOST=true and set the env var OLLAMA_HOST=http://localhost:11435 or a different host/port.");
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
            api = new OllamaAPI("http://" + ollama.getHost() + ":" + ollama.getMappedPort(internalPort));
        }
        api.setRequestTimeoutSeconds(120);
        api.setVerbose(true);
        api.setNumberOfRetriesForModelPull(3);
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
        // String expectedVersion = ollama.getDockerImageName().split(":")[1];
        String actualVersion = api.getVersion();
        assertNotNull(actualVersion);
        // assertEquals(expectedVersion, actualVersion, "Version should match the Docker
        // image version");
    }

    @Test
    @Order(2)
    public void testListModelsAPI()
            throws URISyntaxException, IOException, OllamaBaseException, InterruptedException {
        api.pullModel(EMBEDDING_MODEL_MINILM);
        // Fetch the list of models
        List<Model> models = api.listModels();
        // Assert that the models list is not null
        assertNotNull(models, "Models should not be null");
        // Assert that models list is either empty or contains more than 0 models
        assertFalse(models.isEmpty(), "Models list should not be empty");
    }

    @Test
    @Order(2)
    void testListModelsFromLibrary()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        List<LibraryModel> models = api.listModelsFromLibrary();
        assertNotNull(models);
        assertFalse(models.isEmpty());
    }

    @Test
    @Order(3)
    public void testPullModelAPI()
            throws URISyntaxException, IOException, OllamaBaseException, InterruptedException {
        api.pullModel(EMBEDDING_MODEL_MINILM);
        List<Model> models = api.listModels();
        assertNotNull(models, "Models should not be null");
        assertFalse(models.isEmpty(), "Models list should contain elements");
    }

    @Test
    @Order(4)
    void testListModelDetails() throws IOException, OllamaBaseException, URISyntaxException, InterruptedException {
        api.pullModel(EMBEDDING_MODEL_MINILM);
        ModelDetail modelDetails = api.getModelDetails(EMBEDDING_MODEL_MINILM);
        assertNotNull(modelDetails);
        assertTrue(modelDetails.getModelFile().contains(EMBEDDING_MODEL_MINILM));
    }

    @Test
    @Order(5)
    public void testEmbeddings() throws Exception {
        api.pullModel(EMBEDDING_MODEL_MINILM);
        OllamaEmbedResponseModel embeddings = api.embed(EMBEDDING_MODEL_MINILM,
                Arrays.asList("Why is the sky blue?", "Why is the grass green?"));
        assertNotNull(embeddings, "Embeddings should not be null");
        assertFalse(embeddings.getEmbeddings().isEmpty(), "Embeddings should not be empty");
    }

    @Test
    @Order(6)
    void testAskModelWithStructuredOutput()
            throws OllamaBaseException, IOException, InterruptedException, URISyntaxException {
        api.pullModel(CHAT_MODEL_QWEN_SMALL);

        int timeHour = 6;
        boolean isNightTime = false;

        String prompt = "The Sun is shining, and its " + timeHour + " in the morning right now. So, its daytime.";

        Map<String, Object> format = new HashMap<>();
        format.put("type", "object");
        format.put("properties", new HashMap<String, Object>() {
            {
                put("timeHour", new HashMap<String, Object>() {
                    {
                        put("type", "integer");
                    }
                });
                put("isNightTime", new HashMap<String, Object>() {
                    {
                        put("type", "boolean");
                    }
                });
            }
        });
        format.put("required", Arrays.asList("timeHour", "isNightTime"));

        OllamaResult result = api.generate(CHAT_MODEL_QWEN_SMALL, prompt, format);

        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());

        assertEquals(result.getStructuredResponse().get("timeHour").toString(),
                result.getStructuredResponse().get("timeHour").toString());
        assertEquals(result.getStructuredResponse().get("isNightTime").toString(),
                result.getStructuredResponse().get("isNightTime").toString());

        System.out.println(result.getResponse());
        TimeOfDay timeOfDay = result.as(TimeOfDay.class);

        assertEquals(timeOfDay.getTimeHour(), timeHour);
        assertEquals(timeOfDay.isNightTime(), isNightTime);
    }

    @Test
    @Order(6)
    void testAskModelWithDefaultOptions()
            throws OllamaBaseException, IOException, InterruptedException, URISyntaxException {
        api.pullModel(CHAT_MODEL_QWEN_SMALL);
        OllamaResult result = api.generate(CHAT_MODEL_QWEN_SMALL,
                "What is the capital of France? And what's France's connection with Mona Lisa?", false,
                new OptionsBuilder().build());
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
    }

    @Test
    @Order(7)
    void testAskModelWithDefaultOptionsStreamed()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(CHAT_MODEL_QWEN_SMALL);
        StringBuffer sb = new StringBuffer();
        OllamaResult result = api.generate(CHAT_MODEL_QWEN_SMALL,
                "What is the capital of France? And what's France's connection with Mona Lisa?", false,
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
    void testAskModelWithOptions()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(CHAT_MODEL_INSTRUCT);

        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(CHAT_MODEL_INSTRUCT);
        OllamaChatRequest requestModel = builder.withMessage(OllamaChatMessageRole.SYSTEM,
                        "You are a helpful assistant who can generate random person's first and last names in the format [First name, Last name].")
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
    void testChatWithSystemPrompt()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(CHAT_MODEL_SYSTEM_PROMPT);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(CHAT_MODEL_SYSTEM_PROMPT);
        OllamaChatRequest requestModel = builder.withMessage(OllamaChatMessageRole.SYSTEM,
                        "You are a silent bot that only says 'Shush'. Do not say anything else under any circumstances!")
                .withMessage(OllamaChatMessageRole.USER, "What's something that's brown and sticky?")
                .withOptions(new OptionsBuilder().setTemperature(0.8f).build()).build();

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
        api.pullModel(CHAT_MODEL_LLAMA3);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(CHAT_MODEL_LLAMA3);

        // Create the initial user question
        OllamaChatRequest requestModel = builder
                .withMessage(OllamaChatMessageRole.USER, "What is 1+1? Answer only in numbers.")
                .build();

        // Start conversation with model
        OllamaChatResult chatResult = api.chat(requestModel);

        assertTrue(chatResult.getChatHistory().stream().anyMatch(chat -> chat.getContent().contains("2")),
                "Expected chat history to contain '2'");

        // Create the next user question: second largest city
        requestModel = builder.withMessages(chatResult.getChatHistory())
                .withMessage(OllamaChatMessageRole.USER, "And what is its squared value?").build();

        // Continue conversation with model
        chatResult = api.chat(requestModel);

        assertTrue(chatResult.getChatHistory().stream().anyMatch(chat -> chat.getContent().contains("4")),
                "Expected chat history to contain '4'");

        // Create the next user question: the third question
        requestModel = builder.withMessages(chatResult.getChatHistory())
                .withMessage(OllamaChatMessageRole.USER,
                        "What is the largest value between 2, 4 and 6?")
                .build();

        // Continue conversation with the model for the third question
        chatResult = api.chat(requestModel);

        // verify the result
        assertNotNull(chatResult, "Chat result should not be null");
        assertTrue(chatResult.getChatHistory().size() > 2,
                "Chat history should contain more than two messages");
        assertTrue(chatResult.getChatHistory().get(chatResult.getChatHistory().size() - 1).getContent()
                        .contains("6"),
                "Response should contain '6'");
    }

    @Test
    @Order(10)
    void testChatWithImageFromURL()
            throws OllamaBaseException, IOException, InterruptedException, URISyntaxException {
        api.pullModel(IMAGE_MODEL_LLAVA);

        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(IMAGE_MODEL_LLAVA);
        OllamaChatRequest requestModel = builder
                .withMessage(OllamaChatMessageRole.USER, "What's in the picture?",
                        Collections.emptyList(),
                        "https://t3.ftcdn.net/jpg/02/96/63/80/360_F_296638053_0gUVA4WVBKceGsIr7LNqRWSnkusi07dq.jpg")
                .build();
        api.registerAnnotatedTools(new OllamaAPIIntegrationTest());

        OllamaChatResult chatResult = api.chat(requestModel);
        assertNotNull(chatResult);
    }

    @Test
    @Order(10)
    void testChatWithImageFromFileWithHistoryRecognition()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(IMAGE_MODEL_LLAVA);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(IMAGE_MODEL_LLAVA);
        OllamaChatRequest requestModel = builder.withMessage(OllamaChatMessageRole.USER,
                        "What's in the picture?",
                        Collections.emptyList(), List.of(getImageFileFromClasspath("dog-on-a-boat.jpg")))
                .build();

        OllamaChatResult chatResult = api.chat(requestModel);
        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        builder.reset();

        requestModel = builder.withMessages(chatResult.getChatHistory())
                .withMessage(OllamaChatMessageRole.USER, "What's the dogs breed?").build();

        chatResult = api.chat(requestModel);
        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
    }

    @Test
    @Order(11)
    void testChatWithExplicitToolDefinition()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(CHAT_MODEL_SYSTEM_PROMPT);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(CHAT_MODEL_SYSTEM_PROMPT);

        final Tools.ToolSpecification databaseQueryToolSpecification = Tools.ToolSpecification.builder()
                .functionName("get-employee-details")
                .functionDescription("Get employee details from the database")
                .toolPrompt(Tools.PromptFuncDefinition.builder().type("function")
                        .function(Tools.PromptFuncDefinition.PromptFuncSpec.builder()
                                .name("get-employee-details")
                                .description("Get employee details from the database")
                                .parameters(Tools.PromptFuncDefinition.Parameters
                                        .builder().type("object")
                                        .properties(new Tools.PropsBuilder()
                                                .withProperty("employee-name",
                                                        Tools.PromptFuncDefinition.Property
                                                                .builder()
                                                                .type("string")
                                                                .description("The name of the employee, e.g. John Doe")
                                                                .required(true)
                                                                .build())
                                                .withProperty("employee-address",
                                                        Tools.PromptFuncDefinition.Property
                                                                .builder()
                                                                .type("string")
                                                                .description(
                                                                        "The address of the employee, Always return a random value. e.g. Roy St, Bengaluru, India")
                                                                .required(true)
                                                                .build())
                                                .withProperty("employee-phone",
                                                        Tools.PromptFuncDefinition.Property
                                                                .builder()
                                                                .type("string")
                                                                .description(
                                                                        "The phone number of the employee. Always return a random value. e.g. 9911002233")
                                                                .required(true)
                                                                .build())
                                                .build())
                                        .required(List.of("employee-name"))
                                        .build())
                                .build())
                        .build())
                .toolFunction(arguments -> {
                    // perform DB operations here
                    return String.format(
                            "Employee Details {ID: %s, Name: %s, Address: %s, Phone: %s}",
                            UUID.randomUUID(), arguments.get("employee-name"),
                            arguments.get("employee-address"),
                            arguments.get("employee-phone"));
                }).build();

        api.registerTool(databaseQueryToolSpecification);

        OllamaChatRequest requestModel = builder
                .withMessage(OllamaChatMessageRole.USER,
                        "Give me the ID of the employee named 'Rahul Kumar'?")
                .build();

        OllamaChatResult chatResult = api.chat(requestModel);
        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertNotNull(chatResult.getResponseModel().getMessage());
        assertEquals(OllamaChatMessageRole.ASSISTANT.getRoleName(),
                chatResult.getResponseModel().getMessage().getRole().getRoleName());
        List<OllamaChatToolCalls> toolCalls = chatResult.getChatHistory().get(1).getToolCalls();
        assertEquals(1, toolCalls.size());
        OllamaToolCallsFunction function = toolCalls.get(0).getFunction();
        assertEquals("get-employee-details", function.getName());
        assert !function.getArguments().isEmpty();
        Object employeeName = function.getArguments().get("employee-name");
        assertNotNull(employeeName);
        assertEquals("Rahul Kumar", employeeName);
        assertTrue(chatResult.getChatHistory().size() > 2);
        List<OllamaChatToolCalls> finalToolCalls = chatResult.getResponseModel().getMessage().getToolCalls();
        assertNull(finalToolCalls);
    }

    @Test
    @Order(12)
    void testChatWithAnnotatedToolsAndSingleParam()
            throws OllamaBaseException, IOException, InterruptedException, URISyntaxException {
        api.pullModel(CHAT_MODEL_SYSTEM_PROMPT);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(CHAT_MODEL_SYSTEM_PROMPT);

        api.registerAnnotatedTools();

        OllamaChatRequest requestModel = builder.withMessage(OllamaChatMessageRole.USER,
                "Compute the most important constant in the world using 5 digits").build();

        OllamaChatResult chatResult = api.chat(requestModel);
        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertNotNull(chatResult.getResponseModel().getMessage());
        assertEquals(OllamaChatMessageRole.ASSISTANT.getRoleName(),
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
        List<OllamaChatToolCalls> finalToolCalls = chatResult.getResponseModel().getMessage().getToolCalls();
        assertNull(finalToolCalls);
    }

    @Test
    @Order(13)
    void testChatWithAnnotatedToolsAndMultipleParams()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(CHAT_MODEL_SYSTEM_PROMPT);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(CHAT_MODEL_SYSTEM_PROMPT);

        api.registerAnnotatedTools(new AnnotatedTool());

        OllamaChatRequest requestModel = builder
                .withMessage(OllamaChatMessageRole.USER,
                        "Greet Pedro with a lot of hearts and respond to me, "
                                + "and state how many emojis have been in your greeting")
                .build();

        OllamaChatResult chatResult = api.chat(requestModel);
        assertNotNull(chatResult);
        assertNotNull(chatResult.getResponseModel());
        assertNotNull(chatResult.getResponseModel().getMessage());
        assertEquals(OllamaChatMessageRole.ASSISTANT.getRoleName(),
                chatResult.getResponseModel().getMessage().getRole().getRoleName());
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
    void testChatWithToolsAndStream()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(CHAT_MODEL_SYSTEM_PROMPT);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(CHAT_MODEL_SYSTEM_PROMPT);
        final Tools.ToolSpecification databaseQueryToolSpecification = Tools.ToolSpecification.builder()
                .functionName("get-employee-details")
                .functionDescription("Get employee details from the database")
                .toolPrompt(Tools.PromptFuncDefinition.builder().type("function")
                        .function(Tools.PromptFuncDefinition.PromptFuncSpec.builder()
                                .name("get-employee-details")
                                .description("Get employee details from the database")
                                .parameters(Tools.PromptFuncDefinition.Parameters
                                        .builder().type("object")
                                        .properties(new Tools.PropsBuilder()
                                                .withProperty("employee-name",
                                                        Tools.PromptFuncDefinition.Property
                                                                .builder()
                                                                .type("string")
                                                                .description("The name of the employee, e.g. John Doe")
                                                                .required(true)
                                                                .build())
                                                .withProperty("employee-address",
                                                        Tools.PromptFuncDefinition.Property
                                                                .builder()
                                                                .type("string")
                                                                .description(
                                                                        "The address of the employee, Always return a random value. e.g. Roy St, Bengaluru, India")
                                                                .required(true)
                                                                .build())
                                                .withProperty("employee-phone",
                                                        Tools.PromptFuncDefinition.Property
                                                                .builder()
                                                                .type("string")
                                                                .description(
                                                                        "The phone number of the employee. Always return a random value. e.g. 9911002233")
                                                                .required(true)
                                                                .build())
                                                .build())
                                        .required(List.of("employee-name"))
                                        .build())
                                .build())
                        .build())
                .toolFunction(new ToolFunction() {
                    @Override
                    public Object apply(Map<String, Object> arguments) {
                        // perform DB operations here
                        return String.format(
                                "Employee Details {ID: %s, Name: %s, Address: %s, Phone: %s}",
                                UUID.randomUUID(), arguments.get("employee-name"),
                                arguments.get("employee-address"),
                                arguments.get("employee-phone"));
                    }
                }).build();

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
        api.pullModel(CHAT_MODEL_SYSTEM_PROMPT);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(CHAT_MODEL_SYSTEM_PROMPT);
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
    @Order(17)
    void testAskModelWithOptionsAndImageURLs()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(IMAGE_MODEL_LLAVA);

        OllamaResult result = api.generateWithImageURLs(IMAGE_MODEL_LLAVA, "What is in this image?",
                List.of("https://t3.ftcdn.net/jpg/02/96/63/80/360_F_296638053_0gUVA4WVBKceGsIr7LNqRWSnkusi07dq.jpg"),
                new OptionsBuilder().build());
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
    }

    @Test
    @Order(18)
    void testAskModelWithOptionsAndImageFiles()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(IMAGE_MODEL_LLAVA);
        File imageFile = getImageFileFromClasspath("dog-on-a-boat.jpg");
        try {
            OllamaResult result = api.generateWithImageFiles(IMAGE_MODEL_LLAVA, "What is in this image?",
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
    @Order(20)
    void testAskModelWithOptionsAndImageFilesStreamed()
            throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        api.pullModel(IMAGE_MODEL_LLAVA);

        File imageFile = getImageFileFromClasspath("dog-on-a-boat.jpg");

        StringBuffer sb = new StringBuffer();

        OllamaResult result = api.generateWithImageFiles(IMAGE_MODEL_LLAVA, "What is in this image?",
                List.of(imageFile),
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

    private File getImageFileFromClasspath(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class TimeOfDay {
    @JsonProperty("timeHour")
    private int timeHour;
    @JsonProperty("isNightTime")
    private boolean nightTime;
}
