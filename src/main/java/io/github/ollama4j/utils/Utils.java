/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    private static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
        }
        return objectMapper;
    }

    public static byte[] loadImageBytesFromUrl(
            String imageUrl, int connectTimeoutSeconds, int readTimeoutSeconds)
            throws IOException, InterruptedException {
        LOG.debug(
                "Attempting to load image from URL: {} (connectTimeout={}s, readTimeout={}s)",
                imageUrl,
                connectTimeoutSeconds,
                readTimeoutSeconds);
        HttpClient client =
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(connectTimeoutSeconds))
                        .build();
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(imageUrl))
                        .timeout(Duration.ofSeconds(readTimeoutSeconds))
                        .header("User-Agent", "Mozilla/5.0")
                        .GET()
                        .build();
        LOG.debug("Sending HTTP GET request to {}", imageUrl);
        HttpResponse<byte[]> response =
                client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        LOG.debug("Received HTTP response with status code: {}", response.statusCode());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            LOG.debug(
                    "Successfully loaded image from URL: {} ({} bytes)",
                    imageUrl,
                    response.body().length);
            return response.body();
        } else {
            LOG.error(
                    "Failed to load image from URL: {}. HTTP status: {}",
                    imageUrl,
                    response.statusCode());
            throw new IOException("Failed to load image: HTTP " + response.statusCode());
        }
    }

    public static File getFileFromClasspath(String fileName) {
        LOG.debug("Trying to load file from classpath: {}", fileName);
        ClassLoader classLoader = Utils.class.getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
    }
}
