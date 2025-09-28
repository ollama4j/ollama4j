/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.generate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OllamaGenerateResponseModel {
    private String model;
    private @JsonProperty("created_at") String createdAt;
    private @JsonProperty("done_reason") String doneReason;
    private boolean done;
    private List<Integer> context;
    private @JsonProperty("total_duration") Long totalDuration;
    private @JsonProperty("load_duration") Long loadDuration;
    private @JsonProperty("prompt_eval_duration") Long promptEvalDuration;
    private @JsonProperty("eval_duration") Long evalDuration;
    private @JsonProperty("prompt_eval_count") Integer promptEvalCount;
    private @JsonProperty("eval_count") Integer evalCount;
    private String response;
    private String thinking;
}
