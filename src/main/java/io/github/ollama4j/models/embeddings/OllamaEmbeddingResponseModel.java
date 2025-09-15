package io.github.ollama4j.models.embeddings;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@SuppressWarnings("unused")
@Data
@Deprecated(since = "1.0.90")
public class OllamaEmbeddingResponseModel {
    @JsonProperty("embedding")
    private List<Double> embedding;
}
