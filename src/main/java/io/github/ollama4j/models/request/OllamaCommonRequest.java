/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ollama4j.utils.Utils;
import java.util.Map;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class OllamaCommonRequest {

    protected String model;

    /**
     * The value can either be
     *     <pre>{@code json }</pre>
     * or
     *     <pre>{@code {"key1": "val1", "key2": "val2"} }</pre>
     */
    @JsonProperty(value = "format", required = false, defaultValue = "json")
    protected Object format;

    protected Map<String, Object> options;
    protected String template;
    protected boolean stream;

    @JsonProperty(value = "keep_alive")
    protected String keepAlive;

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
