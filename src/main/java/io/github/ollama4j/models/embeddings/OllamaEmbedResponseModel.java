package io.github.ollama4j.models.embeddings;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@SuppressWarnings("unused")
@Data
public class OllamaEmbedResponseModel {
    @JsonProperty("model")
    private String model;

    @JsonProperty("embeddings")
    private List<List<Double>> embeddings;

    @JsonProperty("total_duration")
    private long totalDuration;

    @JsonProperty("load_duration")
    private long loadDuration;

    @JsonProperty("prompt_eval_count")
    private int promptEvalCount;
}
