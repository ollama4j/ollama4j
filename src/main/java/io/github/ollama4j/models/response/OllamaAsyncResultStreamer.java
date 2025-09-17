/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.response;

import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.generate.OllamaGenerateRequest;
import io.github.ollama4j.models.generate.OllamaGenerateResponseModel;
import io.github.ollama4j.utils.Constants;
import io.github.ollama4j.utils.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("unused")
public class OllamaAsyncResultStreamer extends Thread {
    private final HttpRequest.Builder requestBuilder;
    private final OllamaGenerateRequest ollamaRequestModel;
    private final OllamaResultStream thinkingResponseStream = new OllamaResultStream();
    private final OllamaResultStream responseStream = new OllamaResultStream();
    private String completeResponse;
    private String completeThinkingResponse;

    /**
     * -- GETTER -- Returns the status of the request. Indicates if the request was successful or a
     * failure. If the request was a failure, the `getResponse()` method will return the error
     * message.
     */
    @Getter private boolean succeeded;

    @Setter private long requestTimeoutSeconds;

    /**
     * -- GETTER -- Returns the HTTP response status code for the request that was made to Ollama
     * server.
     */
    @Getter private int httpStatusCode;

    /**
     * -- GETTER -- Returns the response time in milliseconds.
     */
    @Getter private long responseTime = 0;

    public OllamaAsyncResultStreamer(
            HttpRequest.Builder requestBuilder,
            OllamaGenerateRequest ollamaRequestModel,
            long requestTimeoutSeconds) {
        this.requestBuilder = requestBuilder;
        this.ollamaRequestModel = ollamaRequestModel;
        this.completeResponse = "";
        this.responseStream.add("");
        this.requestTimeoutSeconds = requestTimeoutSeconds;
    }

    @Override
    public void run() {
        ollamaRequestModel.setStream(true);
        HttpClient httpClient = HttpClient.newHttpClient();
        long startTime = System.currentTimeMillis();
        try {
            HttpRequest request =
                    requestBuilder
                            .POST(
                                    HttpRequest.BodyPublishers.ofString(
                                            Utils.getObjectMapper()
                                                    .writeValueAsString(ollamaRequestModel)))
                            .header(
                                    Constants.HttpConstants.HEADER_KEY_CONTENT_TYPE,
                                    Constants.HttpConstants.APPLICATION_JSON)
                            .timeout(Duration.ofSeconds(requestTimeoutSeconds))
                            .build();
            HttpResponse<InputStream> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            int statusCode = response.statusCode();
            this.httpStatusCode = statusCode;

            InputStream responseBodyStream = response.body();
            BufferedReader reader = null;
            try {
                reader =
                        new BufferedReader(
                                new InputStreamReader(responseBodyStream, StandardCharsets.UTF_8));
                String line;
                StringBuilder thinkingBuffer = new StringBuilder();
                StringBuilder responseBuffer = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    if (statusCode == 404) {
                        OllamaErrorResponse ollamaResponseModel =
                                Utils.getObjectMapper().readValue(line, OllamaErrorResponse.class);
                        responseStream.add(ollamaResponseModel.getError());
                        responseBuffer.append(ollamaResponseModel.getError());
                    } else {
                        OllamaGenerateResponseModel ollamaResponseModel =
                                Utils.getObjectMapper()
                                        .readValue(line, OllamaGenerateResponseModel.class);
                        String thinkingTokens = ollamaResponseModel.getThinking();
                        String responseTokens = ollamaResponseModel.getResponse();
                        if (thinkingTokens == null) {
                            thinkingTokens = "";
                        }
                        if (responseTokens == null) {
                            responseTokens = "";
                        }
                        thinkingResponseStream.add(thinkingTokens);
                        responseStream.add(responseTokens);
                        if (!ollamaResponseModel.isDone()) {
                            responseBuffer.append(responseTokens);
                            thinkingBuffer.append(thinkingTokens);
                        }
                    }
                }
                this.succeeded = true;
                this.completeThinkingResponse = thinkingBuffer.toString();
                this.completeResponse = responseBuffer.toString();
                long endTime = System.currentTimeMillis();
                responseTime = endTime - startTime;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        // Optionally log or handle
                    }
                }
                if (responseBodyStream != null) {
                    try {
                        responseBodyStream.close();
                    } catch (IOException e) {
                        // Optionally log or handle
                    }
                }
            }
            if (statusCode != 200) {
                throw new OllamaBaseException(this.completeResponse);
            }
        } catch (IOException | InterruptedException | OllamaBaseException e) {
            this.succeeded = false;
            this.completeResponse = "[FAILED] " + e.getMessage();
        }
    }
}
