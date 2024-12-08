package io.github.ollama4j.unittests.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.generate.OllamaGenerateRequest;
import io.github.ollama4j.utils.Utils;
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
    public void testOllamaRequestSerialization() throws Exception {

        class SimpleClass {
            private String parameter;

            public SimpleClass() {
                parameter = "test";
            }

            public String getParameter() {
                return parameter;
            }

            public void setParameter(String parameter) {
                this.parameter = parameter;
            }
        }

        OllamaGenerateRequest req = builder.withPrompt("Some prompt").withResponseClass(SimpleClass.class).build();
        String jsonRequest = serialize(req);

        JsonNode rootNode = Utils.getObjectMapper().readTree(jsonRequest);
        assertNotNull(rootNode.get("format"),
                "Request should contain a 'format' property when responseClass is provided");
    }

}
