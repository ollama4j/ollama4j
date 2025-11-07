/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests;

import static org.junit.jupiter.api.Assertions.*;

import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.request.ThinkMode;
import org.junit.jupiter.api.Test;

class TestOllamaChatRequestBuilder {

    @Test
    void testResetClearsMessagesButKeepsModelAndThink() {
        OllamaChatRequest builder =
                OllamaChatRequest.builder()
                        .withModel("my-model")
                        .withThinking(ThinkMode.ENABLED)
                        .withMessage(OllamaChatMessageRole.USER, "first");

        OllamaChatRequest beforeReset = builder.build();
        assertEquals("my-model", beforeReset.getModel());
        assertEquals(ThinkMode.ENABLED, beforeReset.getThink());
        assertEquals(1, beforeReset.getMessages().size());

        builder.reset();
        OllamaChatRequest afterReset = builder.build();
        assertEquals("my-model", afterReset.getModel());
        assertEquals(ThinkMode.ENABLED, afterReset.getThink());
        assertNotNull(afterReset.getMessages());
        assertEquals(0, afterReset.getMessages().size());
    }
}
