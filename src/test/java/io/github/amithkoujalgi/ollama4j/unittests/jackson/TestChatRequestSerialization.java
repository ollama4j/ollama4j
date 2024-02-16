package io.github.amithkoujalgi.ollama4j.unittests.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatMessageRole;
import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatRequestBuilder;
import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatRequestModel;
import io.github.amithkoujalgi.ollama4j.core.utils.OptionsBuilder;
import io.github.amithkoujalgi.ollama4j.core.utils.Utils;

public class TestChatRequestSerialization {

    private OllamaChatRequestBuilder builder;

    private ObjectMapper mapper = Utils.getObjectMapper();

    @BeforeEach
    public void init() {
        builder = OllamaChatRequestBuilder.getInstance("DummyModel");
    }

    @Test
    public void testRequestOnlyMandatoryFields() {
        OllamaChatRequestModel req = builder.withMessage(OllamaChatMessageRole.USER, "Some prompt",
                List.of(new File("src/test/resources/dog-on-a-boat.jpg"))).build();
        String jsonRequest = serializeRequest(req);
        assertEqualsAfterUnmarshalling(deserializeRequest(jsonRequest), req);
    }

    @Test
    public void testRequestMultipleMessages() {
        OllamaChatRequestModel req = builder.withMessage(OllamaChatMessageRole.SYSTEM, "System prompt")
        .withMessage(OllamaChatMessageRole.USER, "Some prompt")
        .build();
        String jsonRequest = serializeRequest(req);
        assertEqualsAfterUnmarshalling(deserializeRequest(jsonRequest), req);
    }

    @Test
    public void testRequestWithMessageAndImage() {
        OllamaChatRequestModel req = builder.withMessage(OllamaChatMessageRole.USER, "Some prompt",
                List.of(new File("src/test/resources/dog-on-a-boat.jpg"))).build();
        String jsonRequest = serializeRequest(req);
        assertEqualsAfterUnmarshalling(deserializeRequest(jsonRequest), req);
    }

    @Test
    public void testRequestWithOptions() {
        OptionsBuilder b = new OptionsBuilder();
        OllamaChatRequestModel req = builder.withMessage(OllamaChatMessageRole.USER, "Some prompt")
                .withOptions(b.setMirostat(1).build()).build();

        String jsonRequest = serializeRequest(req);
        OllamaChatRequestModel deserializeRequest = deserializeRequest(jsonRequest);
        assertEqualsAfterUnmarshalling(deserializeRequest, req);
        assertEquals(1, deserializeRequest.getOptions().get("mirostat"));
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

    private String serializeRequest(OllamaChatRequestModel req) {
        try {
            return mapper.writeValueAsString(req);
        } catch (JsonProcessingException e) {
            fail("Could not serialize request!", e);
            return null;
        }
    }

    private OllamaChatRequestModel deserializeRequest(String jsonRequest) {
        try {
            return mapper.readValue(jsonRequest, OllamaChatRequestModel.class);
        } catch (JsonProcessingException e) {
            fail("Could not deserialize jsonRequest!", e);
            return null;
        }
    }

    private void assertEqualsAfterUnmarshalling(OllamaChatRequestModel unmarshalledRequest,
            OllamaChatRequestModel req) {
        assertEquals(req, unmarshalledRequest);
    }

}
