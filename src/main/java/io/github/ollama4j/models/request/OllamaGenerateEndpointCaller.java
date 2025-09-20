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
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.generate.OllamaGenerateResponseModel;
import io.github.ollama4j.models.generate.OllamaGenerateStreamObserver;
import io.github.ollama4j.models.generate.OllamaGenerateTokenHandler;
import io.github.ollama4j.models.response.OllamaErrorResponse;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.utils.OllamaRequestBody;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("resource")
public class OllamaGenerateEndpointCaller extends OllamaEndpointCaller {

    private static final Logger LOG = LoggerFactory.getLogger(OllamaGenerateEndpointCaller.class);

    private OllamaGenerateStreamObserver responseStreamObserver;

    public OllamaGenerateEndpointCaller(String host, Auth basicAuth, long requestTimeoutSeconds) {
        super(host, basicAuth, requestTimeoutSeconds);
    }

    @Override
    protected String getEndpointSuffix() {
        return "/api/generate";
    }

    @Override
    protected boolean parseResponseAndAddToBuffer(
            String line, StringBuilder responseBuffer, StringBuilder thinkingBuffer) {
        try {
            OllamaGenerateResponseModel ollamaResponseModel =
                    Utils.getObjectMapper().readValue(line, OllamaGenerateResponseModel.class);
            if (ollamaResponseModel.getResponse() != null) {
                responseBuffer.append(ollamaResponseModel.getResponse());
            }
            if (ollamaResponseModel.getThinking() != null) {
                thinkingBuffer.append(ollamaResponseModel.getThinking());
            }
            if (responseStreamObserver != null) {
                responseStreamObserver.notify(ollamaResponseModel);
            }
            return ollamaResponseModel.isDone();
        } catch (JsonProcessingException e) {
            LOG.error("Error parsing the Ollama chat response!", e);
            return true;
        }
    }

    public OllamaResult call(
            OllamaRequestBody body,
            OllamaGenerateTokenHandler thinkingStreamHandler,
            OllamaGenerateTokenHandler responseStreamHandler)
            throws OllamaBaseException, IOException, InterruptedException {
        responseStreamObserver =
                new OllamaGenerateStreamObserver(thinkingStreamHandler, responseStreamHandler);
        return callSync(body);
    }

    /**
     * Calls the api server on the given host and endpoint suffix asynchronously, aka waiting for the response.
     *
     * @param body POST body payload
     * @return result answer given by the assistant
     * @throws OllamaBaseException  any response code than 200 has been returned
     * @throws IOException          in case the responseStream can not be read
     * @throws InterruptedException in case the server is not reachable or network issues happen
     */
    @SuppressWarnings("DuplicatedCode")
    public OllamaResult callSync(OllamaRequestBody body)
            throws OllamaBaseException, IOException, InterruptedException {
        // Create Request
        long startTime = System.currentTimeMillis();
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create(getHost() + getEndpointSuffix());
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
        OllamaGenerateResponseModel ollamaGenerateResponseModel = null;
        try (BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(responseBodyStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (statusCode == 404) {
                    LOG.warn("Status code: 404 (Not Found)");
                    OllamaErrorResponse ollamaResponseModel =
                            Utils.getObjectMapper().readValue(line, OllamaErrorResponse.class);
                    responseBuffer.append(ollamaResponseModel.getError());
                } else if (statusCode == 401) {
                    LOG.warn("Status code: 401 (Unauthorized)");
                    OllamaErrorResponse ollamaResponseModel =
                            Utils.getObjectMapper()
                                    .readValue(
                                            "{\"error\":\"Unauthorized\"}",
                                            OllamaErrorResponse.class);
                    responseBuffer.append(ollamaResponseModel.getError());
                } else if (statusCode == 400) {
                    LOG.warn("Status code: 400 (Bad Request)");
                    OllamaErrorResponse ollamaResponseModel =
                            Utils.getObjectMapper().readValue(line, OllamaErrorResponse.class);
                    responseBuffer.append(ollamaResponseModel.getError());
                } else {
                    boolean finished =
                            parseResponseAndAddToBuffer(line, responseBuffer, thinkingBuffer);
                    if (finished) {
                        ollamaGenerateResponseModel =
                                Utils.getObjectMapper()
                                        .readValue(line, OllamaGenerateResponseModel.class);
                        break;
                    }
                }
            }
        }

        if (statusCode != 200) {
            LOG.error("Status code: {}", statusCode);
            throw new OllamaBaseException(responseBuffer.toString());
        } else {
            long endTime = System.currentTimeMillis();
            OllamaResult ollamaResult =
                    new OllamaResult(
                            responseBuffer.toString(),
                            thinkingBuffer.toString(),
                            endTime - startTime,
                            statusCode);

            ollamaResult.setModel(ollamaGenerateResponseModel.getModel());
            ollamaResult.setCreatedAt(ollamaGenerateResponseModel.getCreatedAt());
            ollamaResult.setDone(ollamaGenerateResponseModel.isDone());
            ollamaResult.setDoneReason(ollamaGenerateResponseModel.getDoneReason());
            ollamaResult.setContext(ollamaGenerateResponseModel.getContext());
            ollamaResult.setTotalDuration(ollamaGenerateResponseModel.getTotalDuration());
            ollamaResult.setLoadDuration(ollamaGenerateResponseModel.getLoadDuration());
            ollamaResult.setPromptEvalCount(ollamaGenerateResponseModel.getPromptEvalCount());
            ollamaResult.setPromptEvalDuration(ollamaGenerateResponseModel.getPromptEvalDuration());
            ollamaResult.setEvalCount(ollamaGenerateResponseModel.getEvalCount());
            ollamaResult.setEvalDuration(ollamaGenerateResponseModel.getEvalDuration());

            LOG.debug("Model response: {}", ollamaResult);
            return ollamaResult;
        }
    }
}
