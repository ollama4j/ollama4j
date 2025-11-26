/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests.models.response;

import static org.junit.jupiter.api.Assertions.*;

import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.models.response.OllamaStructuredResult;
import java.util.Map;
import lombok.Data;
import org.junit.jupiter.api.Test;

class TestOllamaResult {

    @Data
    static class TestPojo {
        private String key;
    }

    @Test
    void testOllamaResultToString() {
        OllamaResult result = new OllamaResult("response", "thinking", 100, 200);
        result.setModel("model");
        String json = result.toString();
        assertTrue(json.contains("\"response\" : \"response\""));
        assertTrue(json.contains("\"thinking\" : \"thinking\""));
        assertTrue(json.contains("\"httpStatusCode\" : 200"));
    }

    @Test
    void testOllamaResultGetStructuredResponse() {
        OllamaResult result = new OllamaResult("{\"key\":\"value\"}", "thinking", 100, 200);
        Map<String, Object> map = result.getStructuredResponse();
        assertEquals("value", map.get("key"));
    }

    @Test
    void testOllamaResultAs() {
        OllamaResult result = new OllamaResult("{\"key\":\"value\"}", "thinking", 100, 200);
        TestPojo pojo = result.as(TestPojo.class);
        assertEquals("value", pojo.getKey());
    }

    @Test
    void testOllamaResultInvalidJson() {
        OllamaResult result = new OllamaResult("invalid json", "thinking", 100, 200);
        assertThrows(IllegalArgumentException.class, result::getStructuredResponse);
        assertThrows(IllegalArgumentException.class, () -> result.as(TestPojo.class));
    }

    @Test
    void testOllamaResultEmptyResponse() {
        OllamaResult result = new OllamaResult("", "thinking", 100, 200);
        assertThrows(IllegalArgumentException.class, result::getStructuredResponse);
        assertThrows(IllegalArgumentException.class, () -> result.as(TestPojo.class));
    }

    @Test
    void testOllamaStructuredResultToString() {
        OllamaStructuredResult result = new OllamaStructuredResult("{\"key\":\"value\"}", 100, 200);
        String json = result.toString();
        assertTrue(json.contains("\"response\" : \"{\\\"key\\\":\\\"value\\\"}\""));
    }

    @Test
    void testOllamaStructuredResultGetStructuredResponse() {
        OllamaStructuredResult result = new OllamaStructuredResult("{\"key\":\"value\"}", 100, 200);
        Map<String, Object> map = result.getStructuredResponse();
        assertEquals("value", map.get("key"));
    }

    @Test
    void testOllamaStructuredResultGetStructuredResponseClass() {
        OllamaStructuredResult result = new OllamaStructuredResult("{\"key\":\"value\"}", 100, 200);
        TestPojo pojo = result.getStructuredResponse(TestPojo.class);
        assertEquals("value", pojo.getKey());
    }

    @Test
    void testOllamaStructuredResultInvalidJson() {
        OllamaStructuredResult result = new OllamaStructuredResult("invalid json", 100, 200);
        assertThrows(RuntimeException.class, result::getStructuredResponse);
        assertThrows(RuntimeException.class, () -> result.getStructuredResponse(TestPojo.class));
    }
}
