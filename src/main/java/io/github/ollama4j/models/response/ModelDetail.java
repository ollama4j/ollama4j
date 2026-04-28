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
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ollama4j.utils.Utils;
import java.util.Map;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelDetail {
    private String license;

    @JsonProperty("modelfile")
    private String modelFile;

    private String parameters;
    private String template;
    private String system;
    private ModelMeta details;
    private String[] capabilities;

    /**
     * Dynamic model metadata returned by the Ollama API.
     * Values may be of type {@code String}, {@code Number}, {@code Boolean},
     * or nested {@code Map}/{@code List}. Cast accordingly after retrieval.
     */
    @JsonProperty("model_info")
    private Map<String, Object> modelInfo;

    @Override
    public String toString() {
        try {
            return Utils.getObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
