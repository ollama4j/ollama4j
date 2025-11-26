/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests.models.generate;

import static org.junit.jupiter.api.Assertions.*;

import io.github.ollama4j.models.generate.OllamaGenerateRequest;
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

class TestOllamaGenerateRequest {

    @Test
    void testConstructors() {
        OllamaGenerateRequest request1 = new OllamaGenerateRequest();
        assertNull(request1.getModel());

        OllamaGenerateRequest request2 = new OllamaGenerateRequest("model", "prompt");
        assertEquals("model", request2.getModel());
        assertEquals("prompt", request2.getPrompt());

        OllamaGenerateRequest request3 =
                new OllamaGenerateRequest("model", "prompt", Collections.emptyList());
        assertEquals("model", request3.getModel());
        assertEquals("prompt", request3.getPrompt());
        assertTrue(request3.getImages().isEmpty());
    }

    @Test
    void testBuilderMethods() {
        OllamaGenerateRequest request =
                OllamaGenerateRequest.builder()
                        .withModel("model")
                        .withPrompt("prompt")
                        .withSystem("system")
                        .withContext("context")
                        .withTemplate("template")
                        .withStreaming(true)
                        .withKeepAlive("5m")
                        .withRaw(true)
                        .withThink(ThinkMode.ENABLED)
                        .withUseTools(true)
                        .withGetJsonResponse()
                        .withOptions(Options.builder().optionsMap(Collections.emptyMap()).build())
                        .build();

        assertEquals("model", request.getModel());
        assertEquals("prompt", request.getPrompt());
        assertEquals("system", request.getSystem());
        assertEquals("context", request.getContext());
        assertEquals("template", request.getTemplate());
        assertTrue(request.isStream());
        assertEquals("5m", request.getKeepAlive());
        assertTrue(request.isRaw());
        assertEquals(ThinkMode.ENABLED, request.getThink());
        assertTrue(request.isUseTools());
        assertEquals("json", request.getFormat());
    }

    @Test
    void testWithImages(@TempDir File tempDir) throws IOException {
        File imageFile = new File(tempDir, "image.png");
        Files.write(imageFile.toPath(), new byte[] {1, 2, 3});

        List<File> files = new ArrayList<>();
        files.add(imageFile);

        OllamaGenerateRequest request = new OllamaGenerateRequest();
        request.withImages(files);

        assertNotNull(request.getImages());
        assertEquals(1, request.getImages().size());
    }

    @Test
    void testEquals() {
        OllamaGenerateRequest request1 = new OllamaGenerateRequest("model", "prompt");
        OllamaGenerateRequest request2 = new OllamaGenerateRequest("model", "prompt");

        assertEquals(request1, request2);

        request2.setSystem("system");
        assertNotEquals(request1, request2);

        assertNotEquals(request1, new Object());
    }
}
