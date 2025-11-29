/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests.models.request;

import static org.junit.jupiter.api.Assertions.*;

import io.github.ollama4j.models.request.CustomModelFileContentsRequest;
import io.github.ollama4j.models.request.CustomModelFilePathRequest;
import io.github.ollama4j.models.request.CustomModelRequest;
import io.github.ollama4j.models.request.ModelRequest;
import org.junit.jupiter.api.Test;

class TestModelRequest {

    @Test
    void testModelRequest() {
        ModelRequest request = new ModelRequest("model-name");
        assertEquals("model-name", request.getName());
        assertNotNull(request.toString());
    }

    @Test
    void testCustomModelFilePathRequest() {
        CustomModelFilePathRequest request =
                new CustomModelFilePathRequest("model-name", "/path/to/modelfile");
        assertEquals("model-name", request.getName());
        assertEquals("/path/to/modelfile", request.getPath());
    }

    @Test
    void testCustomModelFileContentsRequest() {
        CustomModelFileContentsRequest request =
                new CustomModelFileContentsRequest("model-name", "modelfile contents");
        assertEquals("model-name", request.getName());
        assertEquals("modelfile contents", request.getModelfile());
    }

    @Test
    void testCustomModelRequest() {
        CustomModelRequest request = new CustomModelRequest();
        request.setModel("model-name");
        request.setFrom("base-model");

        assertEquals("model-name", request.getModel());
        assertEquals("base-model", request.getFrom());
        assertTrue(request.getStream());
        assertFalse(request.getQuantize());
    }
}
