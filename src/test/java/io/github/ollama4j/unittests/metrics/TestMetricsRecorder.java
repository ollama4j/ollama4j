/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests.metrics;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import io.github.ollama4j.metrics.MetricsRecorder;
import io.github.ollama4j.models.request.ThinkMode;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class TestMetricsRecorder {

    @Test
    void testRecord() {
        assertDoesNotThrow(
                () ->
                        MetricsRecorder.record(
                                "endpoint",
                                "model",
                                true,
                                ThinkMode.ENABLED,
                                true,
                                Collections.emptyMap(),
                                "json",
                                System.currentTimeMillis(),
                                200,
                                "response"));
    }

    @Test
    void testRecordWithException() {
        assertDoesNotThrow(
                () ->
                        MetricsRecorder.record(
                                "endpoint",
                                "model",
                                false,
                                ThinkMode.DISABLED,
                                false,
                                null,
                                null,
                                System.currentTimeMillis(),
                                500,
                                new RuntimeException("error")));
    }

    @Test
    void testRecordWithMapFormat() {
        assertDoesNotThrow(
                () ->
                        MetricsRecorder.record(
                                "endpoint",
                                "model",
                                false,
                                ThinkMode.DISABLED,
                                false,
                                null,
                                Collections.singletonMap("key", "value"),
                                System.currentTimeMillis(),
                                200,
                                "response"));
    }
}
