/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * The type Ollama result.
 */
@Getter
@Setter
@SuppressWarnings("unused")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OllamaImageResult {
    /**
     * Get the response time in milliseconds.
     */
    private long responseTime = 0;

    private String model;

    @JsonProperty("created_at")
    private String createdAt;

    private Integer completed;
    private Integer total;
    private boolean done;
    private String image;

    @JsonProperty("done_reason")
    private String doneReason;

    @JsonProperty("total_duration")
    private Long totalDuration;

    @JsonProperty("load_duration")
    private Long loadDuration;
}
