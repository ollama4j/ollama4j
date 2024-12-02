package io.github.ollama4j.unittests.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.ollama4j.models.embeddings.OllamaEmbedRequestBuilder;
import io.github.ollama4j.models.embeddings.OllamaEmbedRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.ollama4j.utils.OptionsBuilder;

public class TestEmbedRequestSerialization extends AbstractSerializationTest<OllamaEmbedRequestModel> {

        private OllamaEmbedRequestBuilder builder;

        @BeforeEach
        public void init() {
            builder = OllamaEmbedRequestBuilder.getInstance("DummyModel","DummyPrompt");
        }

            @Test
    public void testRequestOnlyMandatoryFields() {
        OllamaEmbedRequestModel req = builder.build();
        String jsonRequest = serialize(req);
        assertEqualsAfterUnmarshalling(deserialize(jsonRequest,OllamaEmbedRequestModel.class), req);
    }

        @Test
        public void testRequestWithOptions() {
            OptionsBuilder b = new OptionsBuilder();
            OllamaEmbedRequestModel req = builder
                    .withOptions(b.setMirostat(1).build()).build();

            String jsonRequest = serialize(req);
            OllamaEmbedRequestModel deserializeRequest = deserialize(jsonRequest,OllamaEmbedRequestModel.class);
            assertEqualsAfterUnmarshalling(deserializeRequest, req);
            assertEquals(1, deserializeRequest.getOptions().get("mirostat"));
        }
}
