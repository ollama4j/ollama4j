package io.github.amithkoujalgi.ollama4j.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmbeddingResponse {
    @JsonProperty("embedding")
    private double[] embedding;

    public EmbeddingResponse() {
    }

    public double[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(double[] embedding) {
        this.embedding = embedding;
    }
}
