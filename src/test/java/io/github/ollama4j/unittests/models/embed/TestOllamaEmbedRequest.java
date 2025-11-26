/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests.models.embed;

import static org.junit.jupiter.api.Assertions.*;

import io.github.ollama4j.models.embed.OllamaEmbedRequest;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class TestOllamaEmbedRequest {

    @Test
    void testConstructorAndGetters() {
        OllamaEmbedRequest request =
                new OllamaEmbedRequest("model", Arrays.asList("input1", "input2"));

        assertEquals("model", request.getModel());
        assertEquals(2, request.getInput().size());
        assertEquals("input1", request.getInput().get(0));
    }

    @Test
    void testSetters() {
        OllamaEmbedRequest request =
                new OllamaEmbedRequest("model", Collections.singletonList("input"));
        request.setModel("new-model");
        request.setInput(Arrays.asList("new-input1", "new-input2"));
        request.setKeepAlive("5m");
        request.setTruncate(false);

        assertEquals("new-model", request.getModel());
        assertEquals(2, request.getInput().size());
        assertEquals("5m", request.getKeepAlive());
        assertFalse(request.getTruncate());
    }
}
