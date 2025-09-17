/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.ollama4j.models.request.BasicAuth;
import io.github.ollama4j.models.request.BearerAuth;
import org.junit.jupiter.api.Test;

class TestAuth {

    @Test
    void testBasicAuthHeaderEncoding() {
        BasicAuth auth = new BasicAuth("alice", "s3cr3t");
        String header = auth.getAuthHeaderValue();
        assertTrue(header.startsWith("Basic "));
        // "alice:s3cr3t" base64 is "YWxpY2U6czNjcjN0"
        assertEquals("Basic YWxpY2U6czNjcjN0", header);
    }

    @Test
    void testBearerAuthHeaderFormat() {
        BearerAuth auth = new BearerAuth("abc.def.ghi");
        String header = auth.getAuthHeaderValue();
        assertEquals("Bearer abc.def.ghi", header);
    }
}
