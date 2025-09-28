/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.embed;

import io.github.ollama4j.utils.Options;
import java.util.List;

/**
 * Builder class to easily create Requests for Embedding models using ollama.
 */
public class OllamaEmbedRequestBuilder {

    private final OllamaEmbedRequest request;

    private OllamaEmbedRequestBuilder(String model, List<String> input) {
        this.request = new OllamaEmbedRequest(model, input);
    }

    public static OllamaEmbedRequestBuilder getInstance(String model, String... input) {
        return new OllamaEmbedRequestBuilder(model, List.of(input));
    }

    public OllamaEmbedRequestBuilder withOptions(Options options) {
        this.request.setOptions(options.getOptionsMap());
        return this;
    }

    public OllamaEmbedRequestBuilder withKeepAlive(String keepAlive) {
        this.request.setKeepAlive(keepAlive);
        return this;
    }

    public OllamaEmbedRequestBuilder withoutTruncate() {
        this.request.setTruncate(false);
        return this;
    }

    public OllamaEmbedRequest build() {
        return this.request;
    }
}
