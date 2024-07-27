package io.github.ollama4j.models.embeddings;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import lombok.Data;

@SuppressWarnings("unused")
@Data
public class OllamaEmbeddingResponseModel {
    @JsonProperty("embedding")
    private List<Double> embedding;
}
