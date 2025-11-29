/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ollama4j.utils.Utils;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class TestUtils {

    @Test
    void testGetObjectMapper() {
        ObjectMapper mapper1 = Utils.getObjectMapper();
        assertNotNull(mapper1);
        ObjectMapper mapper2 = Utils.getObjectMapper();
        assertSame(mapper1, mapper2);
    }

    @Test
    void testToJSON() throws JsonProcessingException {
        Map<String, String> map = Collections.singletonMap("key", "value");
        String json = Utils.toJSON(map);
        assertTrue(json.contains("\"key\" : \"value\""));
    }

    @Test
    void testLoadImageBytesFromUrl() throws IOException, InterruptedException {
        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            HttpClient.Builder mockBuilder = mock(HttpClient.Builder.class);
            HttpClient mockClient = mock(HttpClient.class);
            HttpResponse<byte[]> mockResponse = mock(HttpResponse.class);

            mockedHttpClient.when(HttpClient::newBuilder).thenReturn(mockBuilder);
            when(mockBuilder.connectTimeout(any())).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockClient);

            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn(new byte[] {1, 2, 3});

            byte[] bytes = Utils.loadImageBytesFromUrl("http://example.com/image.png", 10, 10);
            assertArrayEquals(new byte[] {1, 2, 3}, bytes);
        }
    }

    @Test
    void testLoadImageBytesFromUrlFailure() throws IOException, InterruptedException {
        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            HttpClient.Builder mockBuilder = mock(HttpClient.Builder.class);
            HttpClient mockClient = mock(HttpClient.class);
            HttpResponse<byte[]> mockResponse = mock(HttpResponse.class);

            mockedHttpClient.when(HttpClient::newBuilder).thenReturn(mockBuilder);
            when(mockBuilder.connectTimeout(any())).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockClient);

            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.statusCode()).thenReturn(404);

            assertThrows(
                    IOException.class,
                    () -> {
                        Utils.loadImageBytesFromUrl("http://example.com/image.png", 10, 10);
                    });
        }
    }
}
