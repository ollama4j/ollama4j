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

import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.request.ThinkMode;
import io.github.ollama4j.utils.Options;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TestOllamaChatRequest {

    @Test
    void testConstructors() {
        OllamaChatRequest request1 = new OllamaChatRequest();
        assertTrue(request1.getMessages().isEmpty());

        OllamaChatRequest request2 =
                new OllamaChatRequest("model", ThinkMode.ENABLED, Collections.emptyList());
        assertEquals("model", request2.getModel());
        assertEquals(ThinkMode.ENABLED, request2.getThink());
        assertTrue(request2.getMessages().isEmpty());
    }

    @Test
    void testBuilderMethods() {
        OllamaChatRequest request =
                OllamaChatRequest.builder()
                        .withModel("model")
                        .withMessage(OllamaChatMessageRole.USER, "content")
                        .withTemplate("template")
                        .withStreaming()
                        .withKeepAlive("5m")
                        .withThinking(ThinkMode.ENABLED)
                        .withUseTools(true)
                        .withGetJsonResponse()
                        .withOptions(Options.builder().optionsMap(Collections.emptyMap()).build())
                        .build();

        assertEquals("model", request.getModel());
        assertEquals(1, request.getMessages().size());
        assertEquals("content", request.getMessages().get(0).getResponse());
        assertEquals("template", request.getTemplate());
        assertTrue(request.isStream());
        assertEquals("5m", request.getKeepAlive());
        assertEquals(ThinkMode.ENABLED, request.getThink());
        assertTrue(request.isUseTools());
        assertEquals("json", request.getFormat());
    }

    @Test
    void testWithMessageVariants(@TempDir File tempDir) throws IOException {
        File imageFile = new File(tempDir, "image.png");
        Files.write(imageFile.toPath(), new byte[] {1, 2, 3});
        List<File> images = new ArrayList<>();
        images.add(imageFile);

        OllamaChatRequest request = new OllamaChatRequest();
        request.withMessage(OllamaChatMessageRole.USER, "content1");
        request.withMessage(OllamaChatMessageRole.ASSISTANT, "content2", Collections.emptyList());
        request.withMessage(
                OllamaChatMessageRole.USER, "content3", Collections.emptyList(), images);

        assertEquals(3, request.getMessages().size());
        assertEquals("content1", request.getMessages().get(0).getResponse());
        assertEquals("content2", request.getMessages().get(1).getResponse());
        assertEquals("content3", request.getMessages().get(2).getResponse());
        assertEquals(1, request.getMessages().get(2).getImages().size());
    }

    @Test
    void testReset() {
        OllamaChatRequest request = new OllamaChatRequest();
        request.withMessage(OllamaChatMessageRole.USER, "content");
        assertEquals(1, request.getMessages().size());

        request.reset();
        assertTrue(request.getMessages().isEmpty());
    }

    @Test
    void testEquals() {
        OllamaChatRequest request1 =
                new OllamaChatRequest("model", ThinkMode.ENABLED, Collections.emptyList());
        OllamaChatRequest request2 =
                new OllamaChatRequest("model", ThinkMode.ENABLED, Collections.emptyList());

        assertEquals(request1, request2);

        request2.setModel("other");
        assertNotEquals(request1, request2);

        assertNotEquals(request1, new Object());
    }
}
