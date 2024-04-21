package io.github.amithkoujalgi.ollama4j.unittests.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatMessageRole;
import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatRequestBuilder;
import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatRequestModel;
import io.github.amithkoujalgi.ollama4j.core.utils.OptionsBuilder;

public class TestChatRequestSerialization extends AbstractRequestSerializationTest<OllamaChatRequestModel>{

    private OllamaChatRequestBuilder builder;

    @BeforeEach
    public void init() {
        builder = OllamaChatRequestBuilder.getInstance("DummyModel");
    }

    @Test
    public void testRequestOnlyMandatoryFields() {
        OllamaChatRequestModel req = builder.withMessage(OllamaChatMessageRole.USER, "Some prompt").build();
        String jsonRequest = serializeRequest(req);
        assertEqualsAfterUnmarshalling(deserializeRequest(jsonRequest,OllamaChatRequestModel.class), req);
    }

    @Test
    public void testRequestMultipleMessages() {
        OllamaChatRequestModel req = builder.withMessage(OllamaChatMessageRole.SYSTEM, "System prompt")
        .withMessage(OllamaChatMessageRole.USER, "Some prompt")
        .build();
        String jsonRequest = serializeRequest(req);
        assertEqualsAfterUnmarshalling(deserializeRequest(jsonRequest,OllamaChatRequestModel.class), req);
    }

    @Test
    public void testRequestWithMessageAndImage() {
        OllamaChatRequestModel req = builder.withMessage(OllamaChatMessageRole.USER, "Some prompt",
                List.of(new File("src/test/resources/dog-on-a-boat.jpg"))).build();
        String jsonRequest = serializeRequest(req);
        assertEqualsAfterUnmarshalling(deserializeRequest(jsonRequest,OllamaChatRequestModel.class), req);
    }

    @Test
    public void testRequestWithOptions() {
        OptionsBuilder b = new OptionsBuilder();
        OllamaChatRequestModel req = builder.withMessage(OllamaChatMessageRole.USER, "Some prompt")
            .withOptions(b.setMirostat(1).build())
            .withOptions(b.setTemperature(1L).build())
            .withOptions(b.setMirostatEta(1L).build())
            .withOptions(b.setMirostatTau(1L).build())
            .withOptions(b.setNumGpu(1).build())
            .withOptions(b.setSeed(1).build())
            .withOptions(b.setTopK(1).build())
            .withOptions(b.setTopP(1).build())
            .build();

        String jsonRequest = serializeRequest(req);
        OllamaChatRequestModel deserializeRequest = deserializeRequest(jsonRequest, OllamaChatRequestModel.class);
        assertEqualsAfterUnmarshalling(deserializeRequest, req);
        assertEquals(1, deserializeRequest.getOptions().get("mirostat"));
        assertEquals(1.0, deserializeRequest.getOptions().get("temperature"));
        assertEquals(1.0, deserializeRequest.getOptions().get("mirostat_eta"));
        assertEquals(1.0, deserializeRequest.getOptions().get("mirostat_tau"));
        assertEquals(1, deserializeRequest.getOptions().get("num_gpu"));
        assertEquals(1, deserializeRequest.getOptions().get("seed"));
        assertEquals(1, deserializeRequest.getOptions().get("top_k"));
        assertEquals(1.0, deserializeRequest.getOptions().get("top_p"));
    }

    @Test
    public void testWithJsonFormat() {
        OllamaChatRequestModel req = builder.withMessage(OllamaChatMessageRole.USER, "Some prompt")
                .withGetJsonResponse().build();

        String jsonRequest = serializeRequest(req);
        // no jackson deserialization as format property is not boolean ==> omit as deserialization
        // of request is never used in real code anyways
        JSONObject jsonObject = new JSONObject(jsonRequest);
        String requestFormatProperty = jsonObject.getString("format");
        assertEquals("json", requestFormatProperty);
    }

    @Test
    public void testWithTemplate() {
        OllamaChatRequestModel req = builder.withTemplate("System Template")
            .build();
        String jsonRequest = serializeRequest(req);
        assertEqualsAfterUnmarshalling(deserializeRequest(jsonRequest, OllamaChatRequestModel.class), req);
    }

    @Test
    public void testWithStreaming() {
        OllamaChatRequestModel req = builder.withStreaming().build();
        String jsonRequest = serializeRequest(req);
        assertEquals(deserializeRequest(jsonRequest, OllamaChatRequestModel.class).isStream(), true);
    }

    @Test
    public void testWithKeepAlive() {
        String expectedKeepAlive = "5m";
        OllamaChatRequestModel req = builder.withKeepAlive(expectedKeepAlive)
            .build();
        String jsonRequest = serializeRequest(req);
        assertEquals(deserializeRequest(jsonRequest, OllamaChatRequestModel.class).getKeepAlive(), expectedKeepAlive);
    }
}
