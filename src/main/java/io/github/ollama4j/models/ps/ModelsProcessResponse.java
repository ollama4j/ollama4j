/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.ps;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelsProcessResponse {
    @JsonProperty("models")
    private List<ModelProcess> models;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ModelProcess {
        @JsonProperty("name")
        private String name;

        @JsonProperty("model")
        private String model;

        @JsonProperty("size")
        private long size;

        @JsonProperty("digest")
        private String digest;

        @JsonProperty("details")
        private ModelDetails details;

        @JsonProperty("expires_at")
        private String expiresAt; // Consider using LocalDateTime if you need to process date/time

        @JsonProperty("size_vram")
        private long sizeVram;
    }

    @Data
    @NoArgsConstructor
    public static class ModelDetails {
        @JsonProperty("parent_model")
        private String parentModel;

        @JsonProperty("format")
        private String format;

        @JsonProperty("family")
        private String family;

        @JsonProperty("families")
        private List<String> families;

        @JsonProperty("parameter_size")
        private String parameterSize;

        @JsonProperty("quantization_level")
        private String quantizationLevel;
    }
}
