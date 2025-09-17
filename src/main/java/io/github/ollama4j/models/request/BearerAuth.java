/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BearerAuth extends Auth {
    private String bearerToken;

    /**
     * Get authentication header value.
     *
     * @return authentication header value with bearer token
     */
    public String getAuthHeaderValue() {
        return "Bearer " + bearerToken;
    }
}
