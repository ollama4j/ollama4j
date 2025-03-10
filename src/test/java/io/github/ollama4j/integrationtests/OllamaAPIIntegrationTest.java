package io.github.ollama4j.integrationtests;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.embeddings.OllamaEmbedResponseModel;
import io.github.ollama4j.models.response.LibraryModel;
import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.models.response.ModelDetail;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.utils.OptionsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.testcontainers.ollama.OllamaContainer;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("HttpUrlsUsage")
public class OllamaAPIIntegrationTest {

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
    public void testGenerateEmbeddings() throws Exception {
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
                    System.out.println(s);
                    String substring = s.substring(sb.toString().length(), s.length());
                    System.out.println(substring);
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
}
