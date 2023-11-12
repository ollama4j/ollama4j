package io.github.amithkoujalgi.ollama4j.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EmbeddingResponse {
    @JsonProperty("embedding")
    private List<Double> embedding;

    public EmbeddingResponse() {
    }

    public List<Double> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(List<Double> embedding) {
        this.embedding = embedding;
    }
}
