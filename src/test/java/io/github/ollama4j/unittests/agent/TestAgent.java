/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests.agent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.ollama4j.Ollama;
import io.github.ollama4j.agent.Agent;
import io.github.ollama4j.exceptions.OllamaException;
import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TestAgent {

    @Test
    void testConstructor() {
        Ollama ollama = mock(Ollama.class);
        Agent agent = new Agent("name", ollama, "model", "prompt", Collections.emptyList());
        assertNotNull(agent);
    }

    @Test
    void testInteract() throws OllamaException, IOException {
        Ollama ollama = mock(Ollama.class);
        OllamaChatResult result = mock(OllamaChatResult.class);
        when(result.getChatHistory()).thenReturn(Collections.emptyList());
        when(ollama.chat(any(), any())).thenReturn(result);

        Agent agent = new Agent("name", ollama, "model", "prompt", Collections.emptyList());
        List<OllamaChatMessage> history = agent.interact("hello", null);

        assertNotNull(history);
        verify(ollama, times(1)).chat(any(), any());
    }

    @Test
    void testLoadFromYaml(@TempDir File tempDir) throws IOException {
        File yamlFile = new File(tempDir, "agent.yaml");
        try (FileWriter writer = new FileWriter(yamlFile)) {
            writer.write("name: test-agent\n");
            writer.write("model: test-model\n");
            writer.write("host: http://localhost:11434\n");
            writer.write("tools: []\n");
        }

        // We can't easily test load() because it creates a new Ollama instance
        // internally
        // and tries to pull the model, which will fail without a real Ollama server.
        // However, we can test that it tries to read the file.
        // If we want to test load(), we might need to refactor Agent to allow injecting
        // Ollama factory or mock static methods if possible.
        // Given the current implementation, load() is an integration test candidate or
        // needs heavy mocking.

        // Let's skip load() test for now or try to mock Ollama constructor if possible
        // (not easy with Mockito).
        // Or we can just test the exception path.

        assertThrows(RuntimeException.class, () -> Agent.load("non-existent.yaml"));
    }
}
