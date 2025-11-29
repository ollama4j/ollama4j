/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests.core;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.ollama4j.Ollama;
import io.github.ollama4j.exceptions.OllamaException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class TestOllama {

    @Test
    void testConstructor() {
        Ollama ollama = new Ollama();
        assertNotNull(ollama);

        Ollama ollama2 = new Ollama("http://localhost:11434/");
        assertNotNull(ollama2);
    }

    @Test
    void setAuth() {
        Ollama ollama = new Ollama();
        ollama.setBasicAuth("user", "pass");
        ollama.setBearerAuth("token");
        // No getters for auth, but we verify no exception
    }

    @Test
    void testPing() throws OllamaException, InterruptedException, java.io.IOException {
        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            HttpClient mockClient = mock(HttpClient.class);
            HttpResponse<String> mockResponse = mock(HttpResponse.class);

            mockedHttpClient.when(HttpClient::newHttpClient).thenReturn(mockClient);
            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.statusCode()).thenReturn(200);

            Ollama ollama = new Ollama();
            assertTrue(ollama.ping());
        }
    }

    @Test
    void testPingFailure() throws OllamaException, InterruptedException, java.io.IOException {
        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            HttpClient mockClient = mock(HttpClient.class);
            HttpResponse<String> mockResponse = mock(HttpResponse.class);

            mockedHttpClient.when(HttpClient::newHttpClient).thenReturn(mockClient);
            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.statusCode()).thenReturn(500);

            Ollama ollama = new Ollama();
            assertFalse(ollama.ping());
        }
    }
}
