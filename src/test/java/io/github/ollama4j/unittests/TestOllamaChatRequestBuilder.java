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
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import org.junit.jupiter.api.Test;

class TestOllamaChatRequestBuilder {

    @Test
    void testResetClearsMessagesButKeepsModelAndThink() {
        OllamaChatRequestBuilder builder =
                OllamaChatRequestBuilder.builder()
                        .withModel("my-model")
                        .withThinking(true)
                        .withMessage(OllamaChatMessageRole.USER, "first");

        OllamaChatRequest beforeReset = builder.build();
        assertEquals("my-model", beforeReset.getModel());
        assertTrue(beforeReset.isThink());
        assertEquals(1, beforeReset.getMessages().size());

        builder.reset();
        OllamaChatRequest afterReset = builder.build();
        assertEquals("my-model", afterReset.getModel());
        assertTrue(afterReset.isThink());
        assertNotNull(afterReset.getMessages());
        assertEquals(0, afterReset.getMessages().size());
    }
}
