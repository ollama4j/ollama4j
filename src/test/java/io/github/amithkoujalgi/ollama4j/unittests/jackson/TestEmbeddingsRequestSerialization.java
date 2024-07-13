package io.github.amithkoujalgi.ollama4j.unittests.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.amithkoujalgi.ollama4j.core.models.embeddings.OllamaEmbeddingsRequestModel;
import io.github.amithkoujalgi.ollama4j.core.models.embeddings.OllamaEmbeddingsRequestBuilder;
import io.github.amithkoujalgi.ollama4j.core.utils.OptionsBuilder;

public class TestEmbeddingsRequestSerialization extends AbstractSerializationTest<OllamaEmbeddingsRequestModel> {

        private OllamaEmbeddingsRequestBuilder builder;

        @BeforeEach
        public void init() {
            builder = OllamaEmbeddingsRequestBuilder.getInstance("DummyModel","DummyPrompt");
        }

            @Test
    public void testRequestOnlyMandatoryFields() {
        OllamaEmbeddingsRequestModel req = builder.build();
        String jsonRequest = serialize(req);
        assertEqualsAfterUnmarshalling(deserialize(jsonRequest,OllamaEmbeddingsRequestModel.class), req);
    }

        @Test
        public void testRequestWithOptions() {
            OptionsBuilder b = new OptionsBuilder();
            OllamaEmbeddingsRequestModel req = builder
                    .withOptions(b.setMirostat(1).build()).build();

            String jsonRequest = serialize(req);
            OllamaEmbeddingsRequestModel deserializeRequest = deserialize(jsonRequest,OllamaEmbeddingsRequestModel.class);
            assertEqualsAfterUnmarshalling(deserializeRequest, req);
            assertEquals(1, deserializeRequest.getOptions().get("mirostat"));
        }
}
