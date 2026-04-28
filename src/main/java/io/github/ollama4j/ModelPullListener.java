/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j;

import io.github.ollama4j.models.response.ModelPullResponse;

/**
 * Listener for model pull progress.
 */
@FunctionalInterface
public interface ModelPullListener {
    /**
     * Called when there is a status update during model pull or creation.
     *
     * @param modelName the name of the model
     * @param response the status response containing progress and status
     */
    void onStatusUpdate(String modelName, ModelPullResponse response);
}
