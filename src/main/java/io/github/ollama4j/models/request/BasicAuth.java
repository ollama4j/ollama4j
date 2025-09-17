/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.request;

import java.util.Base64;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BasicAuth extends Auth {
    private String username;
    private String password;

    /**
     * Get basic authentication header value.
     *
     * @return basic authentication header value (encoded credentials)
     */
    public String getAuthHeaderValue() {
        final String credentialsToEncode = this.getUsername() + ":" + this.getPassword();
        return "Basic " + Base64.getEncoder().encodeToString(credentialsToEncode.getBytes());
    }
}
