package io.github.ollama4j.unittests.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.ollama4j.models.generate.OllamaGenerateRequest;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import io.github.ollama4j.models.generate.OllamaGenerateRequestBuilder;
import io.github.ollama4j.utils.OptionsBuilder;

public class TestGenerateRequestSerialization extends AbstractSerializationTest<OllamaGenerateRequest> {

    private OllamaGenerateRequestBuilder builder;

    @BeforeEach
    public void init() {
        builder = OllamaGenerateRequestBuilder.getInstance("DummyModel");
    }

    @Test
    public void testRequestOnlyMandatoryFields() {
        OllamaGenerateRequest req = builder.withPrompt("Some prompt").build();

        String jsonRequest = serialize(req);
        assertEqualsAfterUnmarshalling(deserialize(jsonRequest, OllamaGenerateRequest.class), req);
    }

    @Test
    public void testRequestWithOptions() {
        OptionsBuilder b = new OptionsBuilder();
        OllamaGenerateRequest req =
                builder.withPrompt("Some prompt").withOptions(b.setMirostat(1).build()).build();

        String jsonRequest = serialize(req);
        OllamaGenerateRequest deserializeRequest = deserialize(jsonRequest, OllamaGenerateRequest.class);
        assertEqualsAfterUnmarshalling(deserializeRequest, req);
        assertEquals(1, deserializeRequest.getOptions().get("mirostat"));
    }

    @Test
    public void testWithJsonFormat() {
        OllamaGenerateRequest req =
                builder.withPrompt("Some prompt").withGetJsonResponse().build();

        String jsonRequest = serialize(req);
        // no jackson deserialization as format property is not boolean ==> omit as deserialization
        // of request is never used in real code anyways
        JSONObject jsonObject = new JSONObject(jsonRequest);
        String requestFormatProperty = jsonObject.getString("format");
        assertEquals("json", requestFormatProperty);
    }

}
