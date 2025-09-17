/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.generate;

import java.util.ArrayList;
import java.util.List;

public class OllamaGenerateStreamObserver {

    private final OllamaStreamHandler thinkingStreamHandler;
    private final OllamaStreamHandler responseStreamHandler;

    private final List<OllamaGenerateResponseModel> responseParts = new ArrayList<>();

    private String message = "";

    public OllamaGenerateStreamObserver(
            OllamaStreamHandler thinkingStreamHandler, OllamaStreamHandler responseStreamHandler) {
        this.responseStreamHandler = responseStreamHandler;
        this.thinkingStreamHandler = thinkingStreamHandler;
    }

    public void notify(OllamaGenerateResponseModel currentResponsePart) {
        responseParts.add(currentResponsePart);
        handleCurrentResponsePart(currentResponsePart);
    }

    protected void handleCurrentResponsePart(OllamaGenerateResponseModel currentResponsePart) {
        String response = currentResponsePart.getResponse();
        String thinking = currentResponsePart.getThinking();

        boolean hasResponse = response != null && !response.isEmpty();
        boolean hasThinking = thinking != null && !thinking.isEmpty();

        if (!hasResponse && hasThinking && thinkingStreamHandler != null) {
            // message = message + thinking;

            // use only new tokens received, instead of appending the tokens to the previous
            // ones and sending the full string again
            thinkingStreamHandler.accept(thinking);
        } else if (hasResponse && responseStreamHandler != null) {
            // message = message + response;

            // use only new tokens received, instead of appending the tokens to the previous
            // ones and sending the full string again
            responseStreamHandler.accept(response);
        }
    }
}
