/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import io.github.ollama4j.exceptions.OllamaException;
import io.github.ollama4j.exceptions.ToolInvocationException;
import io.github.ollama4j.exceptions.ToolNotFoundException;
import org.junit.jupiter.api.Test;

class TestExceptions {

    @Test
    void testOllamaException() {
        OllamaException ex1 = new OllamaException("message");
        assertEquals("message", ex1.getMessage());

        Exception cause = new RuntimeException("cause");
        OllamaException ex2 = new OllamaException("message", cause);
        assertEquals("message", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    void testToolInvocationException() {
        ToolInvocationException ex1 = new ToolInvocationException("message");
        assertEquals("message", ex1.getMessage());

        Exception cause = new RuntimeException("cause");
        ToolInvocationException ex2 = new ToolInvocationException("message", cause);
        assertEquals("message", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    void testToolNotFoundException() {
        ToolNotFoundException ex1 = new ToolNotFoundException("message");
        assertEquals("message", ex1.getMessage());
    }
}
