/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.request;

/**
 * Represents the "think" parameter for Ollama API requests.
 * Controls the level or nature of "thinking" performed by the model.
 */
public enum ThinkMode {
    DISABLED(Boolean.FALSE),
    ENABLED(Boolean.TRUE),
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high");

    private final Object value;

    ThinkMode(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
