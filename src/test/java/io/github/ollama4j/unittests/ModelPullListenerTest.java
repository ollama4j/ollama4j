/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2026 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.ollama4j.ModelPullListener;
import io.github.ollama4j.Ollama;
import io.github.ollama4j.models.response.ModelPullResponse;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class ModelPullListenerTest {

    @Test
    void testGlobalListenerNotification() throws Exception {
        Ollama ollama = new Ollama();
        AtomicReference<String> notifiedModel = new AtomicReference<>();
        AtomicReference<ModelPullResponse> notifiedResponse = new AtomicReference<>();

        ModelPullListener listener =
                (modelName, response) -> {
                    notifiedModel.set(modelName);
                    notifiedResponse.set(response);
                };

        ollama.setModelPullListener(listener);

        ModelPullResponse mockResponse = new ModelPullResponse();
        mockResponse.setStatus("pulling");

        Method method =
                Ollama.class.getDeclaredMethod(
                        "processModelPullResponse",
                        ModelPullResponse.class,
                        String.class,
                        ModelPullListener.class);
        method.setAccessible(true);
        method.invoke(ollama, mockResponse, "test-model", null);

        assertEquals("test-model", notifiedModel.get());
        assertEquals("pulling", notifiedResponse.get().getStatus());
    }

    @Test
    void testLocalListenerNotification() throws Exception {
        Ollama ollama = new Ollama();
        AtomicReference<String> notifiedModel = new AtomicReference<>();
        AtomicReference<ModelPullResponse> notifiedResponse = new AtomicReference<>();

        ModelPullListener localListener =
                (modelName, response) -> {
                    notifiedModel.set(modelName);
                    notifiedResponse.set(response);
                };

        ModelPullResponse mockResponse = new ModelPullResponse();
        mockResponse.setStatus("pulling");

        Method method =
                Ollama.class.getDeclaredMethod(
                        "processModelPullResponse",
                        ModelPullResponse.class,
                        String.class,
                        ModelPullListener.class);
        method.setAccessible(true);
        method.invoke(ollama, mockResponse, "test-model", localListener);

        assertEquals("test-model", notifiedModel.get());
        assertEquals("pulling", notifiedResponse.get().getStatus());
    }

    @Test
    void testBothListenersNotification() throws Exception {
        Ollama ollama = new Ollama();
        AtomicBoolean globalCalled = new AtomicBoolean(false);
        AtomicBoolean localCalled = new AtomicBoolean(false);

        ollama.setModelPullListener((modelName, response) -> globalCalled.set(true));
        ModelPullListener localListener = (modelName, response) -> localCalled.set(true);

        ModelPullResponse mockResponse = new ModelPullResponse();
        mockResponse.setStatus("pulling");

        Method method =
                Ollama.class.getDeclaredMethod(
                        "processModelPullResponse",
                        ModelPullResponse.class,
                        String.class,
                        ModelPullListener.class);
        method.setAccessible(true);
        method.invoke(ollama, mockResponse, "test-model", localListener);

        assertTrue(globalCalled.get());
        assertTrue(localCalled.get());
    }
}
