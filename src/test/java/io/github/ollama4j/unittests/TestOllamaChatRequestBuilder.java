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

import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class TestOllamaChatRequestBuilder {

    @Test
    void testResetClearsMessagesButKeepsModelAndThink() {
        OllamaChatRequestBuilder builder =
                OllamaChatRequestBuilder.getInstance("my-model")
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

    @Test
    void testImageUrlFailuresThrowExceptionAndBuilderRemainsUsable() {
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance("m");
        String invalidUrl = "ht!tp:/bad_url"; // clearly invalid URL format

        // Exception should be thrown for invalid URL
        assertThrows(
                Exception.class,
                () -> {
                    builder.withMessage(
                            OllamaChatMessageRole.USER, "hi", Collections.emptyList(), invalidUrl);
                });

        OllamaChatRequest req =
                builder.withMessage(OllamaChatMessageRole.USER, "hello", Collections.emptyList())
                        .build();

        assertNotNull(req.getMessages());
        assert (!req.getMessages().isEmpty());
        OllamaChatMessage msg = req.getMessages().get(0);
        assertNotNull(msg.getContent());
    }
}
