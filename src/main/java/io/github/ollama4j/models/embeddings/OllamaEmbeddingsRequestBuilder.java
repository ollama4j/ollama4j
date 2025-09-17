/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.embeddings;

import io.github.ollama4j.utils.Options;

@Deprecated(since = "1.0.90")
public class OllamaEmbeddingsRequestBuilder {

    private OllamaEmbeddingsRequestBuilder(String model, String prompt) {
        request = new OllamaEmbeddingsRequestModel(model, prompt);
    }

    private OllamaEmbeddingsRequestModel request;

    public static OllamaEmbeddingsRequestBuilder getInstance(String model, String prompt) {
        return new OllamaEmbeddingsRequestBuilder(model, prompt);
    }

    public OllamaEmbeddingsRequestModel build() {
        return request;
    }

    public OllamaEmbeddingsRequestBuilder withOptions(Options options) {
        this.request.setOptions(options.getOptionsMap());
        return this;
    }

    public OllamaEmbeddingsRequestBuilder withKeepAlive(String keepAlive) {
        this.request.setKeepAlive(keepAlive);
        return this;
    }
}
