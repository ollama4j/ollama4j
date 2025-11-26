/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests.models.chat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatResponseModel;
import io.github.ollama4j.models.chat.OllamaChatResult;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class TestOllamaChatResult {

    @Test
    void testConstructorAndGetters() {
        OllamaChatResponseModel responseModel = mock(OllamaChatResponseModel.class);
        OllamaChatMessage message =
                new OllamaChatMessage(OllamaChatMessageRole.ASSISTANT, "response");
        when(responseModel.getMessage()).thenReturn(message);

        List<OllamaChatMessage> history = new ArrayList<>();
        OllamaChatResult result = new OllamaChatResult(responseModel, history);

        assertNotNull(result.getChatHistory());
        assertNotNull(result.getResponseModel());
        assertEquals(1, result.getChatHistory().size());
    }

    @Test
    void testToString() {
        OllamaChatResponseModel responseModel = mock(OllamaChatResponseModel.class);
        OllamaChatMessage message =
                new OllamaChatMessage(OllamaChatMessageRole.ASSISTANT, "response");
        when(responseModel.getMessage()).thenReturn(message);

        List<OllamaChatMessage> history = new ArrayList<>();
        OllamaChatResult result = new OllamaChatResult(responseModel, history);

        assertNotNull(result.toString());
    }
}
