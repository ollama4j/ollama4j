package io.github.amithkoujalgi.ollama4j.unittests.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.amithkoujalgi.ollama4j.core.models.generate.OllamaGenerateRequestBuilder;
import io.github.amithkoujalgi.ollama4j.core.models.generate.OllamaGenerateRequestModel;
import io.github.amithkoujalgi.ollama4j.core.utils.OptionsBuilder;
import io.github.amithkoujalgi.ollama4j.core.utils.Utils;

public class TestGenerateRequestSerialization {

    private OllamaGenerateRequestBuilder builder;

    private ObjectMapper mapper = Utils.getObjectMapper();

    @BeforeEach
    public void init() {
        builder = OllamaGenerateRequestBuilder.getInstance("DummyModel");
    }

    @Test
    public void testRequestOnlyMandatoryFields() {
        OllamaGenerateRequestModel req = builder.withPrompt("Some prompt").build();

        String jsonRequest = serializeRequest(req);
        assertEqualsAfterUnmarshalling(deserializeRequest(jsonRequest), req);
    }

    @Test
    public void testRequestWithOptions() {
        OptionsBuilder b = new OptionsBuilder();
        OllamaGenerateRequestModel req =
                builder.withPrompt("Some prompt").withOptions(b.setMirostat(1).build()).build();

        String jsonRequest = serializeRequest(req);
        OllamaGenerateRequestModel deserializeRequest = deserializeRequest(jsonRequest);
        assertEqualsAfterUnmarshalling(deserializeRequest, req);
        assertEquals(1, deserializeRequest.getOptions().get("mirostat"));
    }

    @Test
    public void testWithJsonFormat() {
        OllamaGenerateRequestModel req =
                builder.withPrompt("Some prompt").withGetJsonResponse().build();

        String jsonRequest = serializeRequest(req);
        // no jackson deserialization as format property is not boolean ==> omit as deserialization
        // of request is never used in real code anyways
        JSONObject jsonObject = new JSONObject(jsonRequest);
        String requestFormatProperty = jsonObject.getString("format");
        assertEquals("json", requestFormatProperty);
    }

    private String serializeRequest(OllamaGenerateRequestModel req) {
        try {
            return mapper.writeValueAsString(req);
        } catch (JsonProcessingException e) {
            fail("Could not serialize request!", e);
            return null;
        }
    }

    private OllamaGenerateRequestModel deserializeRequest(String jsonRequest) {
        try {
            return mapper.readValue(jsonRequest, OllamaGenerateRequestModel.class);
        } catch (JsonProcessingException e) {
            fail("Could not deserialize jsonRequest!", e);
            return null;
        }
    }

    private void assertEqualsAfterUnmarshalling(OllamaGenerateRequestModel unmarshalledRequest,
            OllamaGenerateRequestModel req) {
        assertEquals(req, unmarshalledRequest);
    }

}
