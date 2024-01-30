package io.github.amithkoujalgi.ollama4j.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import lombok.Data;

@SuppressWarnings("unused")
@Data
public class EmbeddingResponse {
    @JsonProperty("embedding")
    private ArrayList<Double> embedding;
}
