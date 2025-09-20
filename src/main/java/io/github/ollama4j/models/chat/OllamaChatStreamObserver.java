/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.chat;

import io.github.ollama4j.models.generate.OllamaGenerateTokenHandler;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OllamaChatStreamObserver implements OllamaChatTokenHandler {
    private OllamaGenerateTokenHandler thinkingStreamHandler;
    private OllamaGenerateTokenHandler responseStreamHandler;

    @Override
    public void accept(OllamaChatResponseModel token) {
        if (responseStreamHandler == null || token == null || token.getMessage() == null) {
            return;
        }

        String thinking = token.getMessage().getThinking();
        String response = token.getMessage().getResponse();

        boolean hasThinking = thinking != null && !thinking.isEmpty();
        boolean hasResponse = response != null && !response.isEmpty();

        if (!hasResponse && hasThinking && thinkingStreamHandler != null) {
            // use only new tokens received, instead of appending the tokens to the previous
            // ones and sending the full string again
            thinkingStreamHandler.accept(thinking);
        } else if (hasResponse) {
            // use only new tokens received, instead of appending the tokens to the previous
            // ones and sending the full string again
            responseStreamHandler.accept(response);
        }
    }
}
