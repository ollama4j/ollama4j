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

import io.github.ollama4j.utils.Options;
import io.github.ollama4j.utils.OptionsBuilder;
import io.github.ollama4j.utils.PromptBuilder;
import io.github.ollama4j.utils.Utils;
import java.io.File;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TestOptionsAndUtils {

    @Test
    void testOptionsBuilderSetsValues() {
        Options options =
                new OptionsBuilder()
                        .setMirostat(1)
                        .setMirostatEta(0.2f)
                        .setMirostatTau(4.5f)
                        .setNumCtx(1024)
                        .setNumGqa(8)
                        .setNumGpu(2)
                        .setNumThread(6)
                        .setRepeatLastN(32)
                        .setRepeatPenalty(1.2f)
                        .setTemperature(0.7f)
                        .setSeed(42)
                        .setStop("STOP")
                        .setTfsZ(1.5f)
                        .setNumPredict(256)
                        .setTopK(50)
                        .setTopP(0.95f)
                        .setMinP(0.05f)
                        .setCustomOption("custom_param", 123)
                        .build();

        Map<String, Object> map = options.getOptionsMap();
        assertEquals(1, map.get("mirostat"));
        assertEquals(0.2f, (Float) map.get("mirostat_eta"), 0.0001);
        assertEquals(4.5f, (Float) map.get("mirostat_tau"), 0.0001);
        assertEquals(1024, map.get("num_ctx"));
        assertEquals(8, map.get("num_gqa"));
        assertEquals(2, map.get("num_gpu"));
        assertEquals(6, map.get("num_thread"));
        assertEquals(32, map.get("repeat_last_n"));
        assertEquals(1.2f, (Float) map.get("repeat_penalty"), 0.0001);
        assertEquals(0.7f, (Float) map.get("temperature"), 0.0001);
        assertEquals(42, map.get("seed"));
        assertEquals("STOP", map.get("stop"));
        assertEquals(1.5f, (Float) map.get("tfs_z"), 0.0001);
        assertEquals(256, map.get("num_predict"));
        assertEquals(50, map.get("top_k"));
        assertEquals(0.95f, (Float) map.get("top_p"), 0.0001);
        assertEquals(0.05f, (Float) map.get("min_p"), 0.0001);
        assertEquals(123, map.get("custom_param"));
    }

    @Test
    void testOptionsBuilderRejectsUnsupportedCustomType() {
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    OptionsBuilder builder = new OptionsBuilder();
                    builder.setCustomOption("bad", new Object());
                });
    }

    @Test
    void testPromptBuilderBuildsExpectedString() {
        String prompt =
                new PromptBuilder()
                        .add("Hello")
                        .addLine(", world!")
                        .addSeparator()
                        .add("Continue.")
                        .build();

        String expected =
                "Hello, world!\n\n--------------------------------------------------\nContinue.";
        assertEquals(expected, prompt);
    }

    @Test
    void testUtilsGetObjectMapperSingletonAndModule() {
        assertSame(Utils.getObjectMapper(), Utils.getObjectMapper());
        // Basic serialization sanity check with JavaTimeModule registered
        assertDoesNotThrow(
                () -> Utils.getObjectMapper().writeValueAsString(java.time.OffsetDateTime.now()));
    }

    @Test
    void testGetFileFromClasspath() {
        File f = Utils.getFileFromClasspath("test-config.properties");
        assertTrue(f.exists());
        assertTrue(f.getName().contains("test-config.properties"));
    }
}
