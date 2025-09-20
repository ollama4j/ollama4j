/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.embeddings;

import static io.github.ollama4j.utils.Utils.getObjectMapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.Map;
import lombok.*;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class OllamaEmbedRequestModel {
    @NonNull private String model;

    @NonNull private List<String> input;

    private Map<String, Object> options;

    @JsonProperty(value = "keep_alive")
    private String keepAlive;

    @JsonProperty(value = "truncate")
    private Boolean truncate = true;

    @Override
    public String toString() {
        try {
            return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
