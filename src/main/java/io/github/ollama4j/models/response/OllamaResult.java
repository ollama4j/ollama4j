/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.response;

import static io.github.ollama4j.utils.Utils.getObjectMapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * The type Ollama result.
 */
@Getter
@Setter
@SuppressWarnings("unused")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OllamaResult {
    /**
     * Get the completion/response text
     */
    private final String response;

    /**
     * Get the thinking text (if available)
     */
    private final String thinking;

    /**
     * Get the response status code.
     */
    private int httpStatusCode;

    /**
     * Get the response time in milliseconds.
     */
    private long responseTime = 0;

    private String model;
    private String createdAt;
    private boolean done;
    private String doneReason;
    private List<Integer> context;
    private Long totalDuration;
    private Long loadDuration;
    private Integer promptEvalCount;
    private Long promptEvalDuration;
    private Integer evalCount;
    private Long evalDuration;

    public OllamaResult(String response, String thinking, long responseTime, int httpStatusCode) {
        this.response = response;
        this.thinking = thinking;
        this.responseTime = responseTime;
        this.httpStatusCode = httpStatusCode;
    }

    @Override
    public String toString() {
        try {
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("response", this.response);
            responseMap.put("thinking", this.thinking);
            responseMap.put("httpStatusCode", this.httpStatusCode);
            responseMap.put("responseTime", this.responseTime);
            responseMap.put("model", this.model);
            responseMap.put("createdAt", this.createdAt);
            responseMap.put("done", this.done);
            responseMap.put("doneReason", this.doneReason);
            responseMap.put("context", this.context);
            responseMap.put("totalDuration", this.totalDuration);
            responseMap.put("loadDuration", this.loadDuration);
            responseMap.put("promptEvalCount", this.promptEvalCount);
            responseMap.put("promptEvalDuration", this.promptEvalDuration);
            responseMap.put("evalCount", this.evalCount);
            responseMap.put("evalDuration", this.evalDuration);
            return getObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(responseMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the structured response if the response is a JSON object.
     *
     * @return Map - structured response
     * @throws IllegalArgumentException if the response is not a valid JSON object
     */
    public Map<String, Object> getStructuredResponse() {
        String responseStr = this.getResponse();
        if (responseStr == null || responseStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Response is empty or null");
        }

        try {
            // Check if the response is a valid JSON
            if ((!responseStr.trim().startsWith("{") && !responseStr.trim().startsWith("["))
                    || (!responseStr.trim().endsWith("}") && !responseStr.trim().endsWith("]"))) {
                throw new IllegalArgumentException("Response is not a valid JSON object");
            }

            return getObjectMapper()
                    .readValue(responseStr, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to parse response as JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Get the structured response mapped to a specific class type.
     *
     * @param <T>   The type of class to map the response to
     * @param clazz The class to map the response to
     * @return An instance of the specified class with the response data
     * @throws IllegalArgumentException if the response is not a valid JSON or is empty
     * @throws RuntimeException         if there is an error mapping the response
     */
    public <T> T as(Class<T> clazz) {
        String responseStr = this.getResponse();
        if (responseStr == null || responseStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Response is empty or null");
        }

        try {
            // Check if the response is a valid JSON
            if ((!responseStr.trim().startsWith("{") && !responseStr.trim().startsWith("["))
                    || (!responseStr.trim().endsWith("}") && !responseStr.trim().endsWith("]"))) {
                throw new IllegalArgumentException("Response is not a valid JSON object");
            }
            return getObjectMapper().readValue(responseStr, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to parse response as JSON: " + e.getMessage(), e);
        }
    }
}
