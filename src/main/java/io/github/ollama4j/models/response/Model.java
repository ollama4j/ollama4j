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
import java.time.OffsetDateTime;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Model {

    private String name;
    private String model;

    @JsonProperty("modified_at")
    private OffsetDateTime modifiedAt;

    @JsonProperty("expires_at")
    private OffsetDateTime expiresAt;

    private String digest;
    private long size;

    @JsonProperty("details")
    private ModelMeta modelMeta;

    /**
     * Returns the model name without its version
     *
     * @return model name
     */
    public String getModelName() {
        return name.split(":")[0];
    }

    /**
     * Returns the model version without its name
     *
     * @return model version
     */
    public String getModelVersion() {
        return name.split(":")[1];
    }

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
