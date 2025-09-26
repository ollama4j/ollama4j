/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.embed;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

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
