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
import lombok.Getter;

@Getter
public class OllamaGenerateStreamObserver {
    private final OllamaGenerateTokenHandler thinkingStreamHandler;
    private final OllamaGenerateTokenHandler responseStreamHandler;

    private final List<OllamaGenerateResponseModel> responseParts = new ArrayList<>();

    public OllamaGenerateStreamObserver(
            OllamaGenerateTokenHandler thinkingStreamHandler,
            OllamaGenerateTokenHandler responseStreamHandler) {
        this.thinkingStreamHandler = thinkingStreamHandler;
        this.responseStreamHandler = responseStreamHandler;
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
            thinkingStreamHandler.accept(thinking);
        } else if (hasResponse && responseStreamHandler != null) {
            responseStreamHandler.accept(response);
        }
    }
}
