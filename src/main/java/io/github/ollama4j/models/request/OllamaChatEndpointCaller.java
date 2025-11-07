/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.ollama4j.exceptions.OllamaException;
import io.github.ollama4j.metrics.MetricsRecorder;
import io.github.ollama4j.models.chat.*;
import io.github.ollama4j.models.chat.OllamaChatTokenHandler;
import io.github.ollama4j.models.response.OllamaErrorResponse;
import io.github.ollama4j.utils.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Specialization class for requests */
@SuppressWarnings("resource")
public class OllamaChatEndpointCaller extends OllamaEndpointCaller {

    private static final Logger LOG = LoggerFactory.getLogger(OllamaChatEndpointCaller.class);
    public static final String endpoint = "/api/chat";

    private OllamaChatTokenHandler tokenHandler;

    public OllamaChatEndpointCaller(String host, Auth auth, long requestTimeoutSeconds) {
        super(host, auth, requestTimeoutSeconds);
    }

    /**
     * Parses streamed Response line from ollama chat. Using {@link
     * com.fasterxml.jackson.databind.ObjectMapper#readValue(String, TypeReference)}
     * should throw
     * {@link IllegalArgumentException} in case of null line or {@link
     * com.fasterxml.jackson.core.JsonParseException} in case the JSON Object cannot
     * be parsed to a
     * {@link OllamaChatResponseModel}. Thus, the ResponseModel should never be
     * null.
     *
     * @param line           streamed line of ollama stream response
     * @param responseBuffer Stringbuffer to add latest response message part to
     * @return TRUE, if ollama-Response has 'done' state
     */
    @Override
    protected boolean parseResponseAndAddToBuffer(
            String line, StringBuilder responseBuffer, StringBuilder thinkingBuffer) {
        try {
            OllamaChatResponseModel ollamaResponseModel =
                    Utils.getObjectMapper().readValue(line, OllamaChatResponseModel.class);
            // It seems that under heavy load Ollama responds with an empty chat message
            // part in the
            // streamed response.
            // Thus, we null check the message and hope that the next streamed response has
            // some
            // message content again.
            OllamaChatMessage message = ollamaResponseModel.getMessage();
            if (message != null) {
                if (message.getThinking() != null) {
                    thinkingBuffer.append(message.getThinking());
                } else {
                    responseBuffer.append(message.getResponse());
                }
                if (tokenHandler != null) {
                    tokenHandler.accept(ollamaResponseModel);
                }
            }
            return ollamaResponseModel.isDone();
        } catch (JsonProcessingException e) {
            LOG.error("Error parsing the Ollama chat response!", e);
            return true;
        }
    }

    public OllamaChatResult call(OllamaChatRequest body, OllamaChatTokenHandler tokenHandler)
            throws OllamaException, IOException, InterruptedException {
        this.tokenHandler = tokenHandler;
        return callSync(body);
    }

    public OllamaChatResult callSync(OllamaChatRequest body)
            throws OllamaException, IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create(getHost() + endpoint);
        HttpRequest.Builder requestBuilder =
                getRequestBuilderDefault(uri).POST(body.getBodyPublisher());
        HttpRequest request = requestBuilder.build();
        LOG.debug("Asking model: {}", body);
        HttpResponse<InputStream> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

        int statusCode = response.statusCode();
        InputStream responseBodyStream = response.body();
        StringBuilder responseBuffer = new StringBuilder();
        StringBuilder thinkingBuffer = new StringBuilder();
        OllamaChatResponseModel ollamaChatResponseModel = null;
        List<OllamaChatToolCalls> wantedToolsForStream = null;

        try (BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(responseBodyStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (handleErrorStatus(statusCode, line, responseBuffer)) {
                    continue;
                }
                boolean finished =
                        parseResponseAndAddToBuffer(line, responseBuffer, thinkingBuffer);
                ollamaChatResponseModel =
                        Utils.getObjectMapper().readValue(line, OllamaChatResponseModel.class);
                if (body.stream
                        && ollamaChatResponseModel.getMessage() != null
                        && ollamaChatResponseModel.getMessage().getToolCalls() != null) {
                    wantedToolsForStream = ollamaChatResponseModel.getMessage().getToolCalls();
                }
                if (finished && body.stream) {
                    ollamaChatResponseModel.getMessage().setResponse(responseBuffer.toString());
                    ollamaChatResponseModel.getMessage().setThinking(thinkingBuffer.toString());
                    break;
                }
            }
        }
        MetricsRecorder.record(
                endpoint,
                body.getModel(),
                false,
                body.getThink(),
                body.isStream(),
                body.getOptions(),
                body.getFormat(),
                startTime,
                statusCode,
                responseBuffer);
        if (statusCode != 200) {
            LOG.error("Status code: {}", statusCode);
            throw new OllamaException(responseBuffer.toString());
        }
        if (wantedToolsForStream != null && ollamaChatResponseModel != null) {
            ollamaChatResponseModel.getMessage().setToolCalls(wantedToolsForStream);
        }
        OllamaChatResult ollamaResult =
                new OllamaChatResult(ollamaChatResponseModel, body.getMessages());
        LOG.debug("Model response: {}", ollamaResult);
        return ollamaResult;
    }

    /**
     * Handles error status codes and appends error messages to the response buffer.
     * Returns true if
     * an error was handled, false otherwise.
     */
    private boolean handleErrorStatus(int statusCode, String line, StringBuilder responseBuffer)
            throws IOException {
        switch (statusCode) {
            case 404:
                LOG.warn("Status code: 404 (Not Found)");
                responseBuffer.append(
                        Utils.getObjectMapper()
                                .readValue(line, OllamaErrorResponse.class)
                                .getError());
                return true;
            case 401:
                LOG.warn("Status code: 401 (Unauthorized)");
                responseBuffer.append(
                        Utils.getObjectMapper()
                                .readValue(
                                        "{\"error\":\"Unauthorized\"}", OllamaErrorResponse.class)
                                .getError());
                return true;
            case 400:
                LOG.warn("Status code: 400 (Bad Request)");
                responseBuffer.append(
                        Utils.getObjectMapper()
                                .readValue(line, OllamaErrorResponse.class)
                                .getError());
                return true;
            case 500:
                LOG.warn("Status code: 500 (Internal Server Error)");
                responseBuffer.append(
                        Utils.getObjectMapper()
                                .readValue(line, OllamaErrorResponse.class)
                                .getError());
                return true;
            default:
                return false;
        }
    }
}
