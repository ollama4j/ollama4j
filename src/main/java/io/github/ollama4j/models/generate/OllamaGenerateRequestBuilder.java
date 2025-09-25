/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.generate;

import io.github.ollama4j.tools.Tools;
import io.github.ollama4j.utils.Options;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/** Helper class for creating {@link OllamaGenerateRequest} objects using the builder-pattern. */
public class OllamaGenerateRequestBuilder {

    private OllamaGenerateRequestBuilder() {
        request = new OllamaGenerateRequest();
    }

    private OllamaGenerateRequest request;

    public static OllamaGenerateRequestBuilder builder() {
        return new OllamaGenerateRequestBuilder();
    }

    public OllamaGenerateRequest build() {
        return request;
    }

    public OllamaGenerateRequestBuilder withPrompt(String prompt) {
        request.setPrompt(prompt);
        return this;
    }

    public OllamaGenerateRequestBuilder withTools(List<Tools.Tool> tools) {
        request.setTools(tools);
        return this;
    }

    public OllamaGenerateRequestBuilder withModel(String model) {
        request.setModel(model);
        return this;
    }

    public OllamaGenerateRequestBuilder withGetJsonResponse() {
        this.request.setFormat("json");
        return this;
    }

    public OllamaGenerateRequestBuilder withOptions(Options options) {
        this.request.setOptions(options.getOptionsMap());
        return this;
    }

    public OllamaGenerateRequestBuilder withTemplate(String template) {
        this.request.setTemplate(template);
        return this;
    }

    public OllamaGenerateRequestBuilder withStreaming(boolean streaming) {
        this.request.setStream(streaming);
        return this;
    }

    public OllamaGenerateRequestBuilder withKeepAlive(String keepAlive) {
        this.request.setKeepAlive(keepAlive);
        return this;
    }

    public OllamaGenerateRequestBuilder withRaw(boolean raw) {
        this.request.setRaw(raw);
        return this;
    }

    public OllamaGenerateRequestBuilder withThink(boolean think) {
        this.request.setThink(think);
        return this;
    }

    public OllamaGenerateRequestBuilder withUseTools(boolean useTools) {
        this.request.setUseTools(useTools);
        return this;
    }

    public OllamaGenerateRequestBuilder withFormat(java.util.Map<String, Object> format) {
        this.request.setFormat(format);
        return this;
    }

    public OllamaGenerateRequestBuilder withSystem(String system) {
        this.request.setSystem(system);
        return this;
    }

    public OllamaGenerateRequestBuilder withContext(String context) {
        this.request.setContext(context);
        return this;
    }

    public OllamaGenerateRequestBuilder withImagesBase64(java.util.List<String> images) {
        this.request.setImages(images);
        return this;
    }

    public OllamaGenerateRequestBuilder withImages(java.util.List<File> imageFiles)
            throws IOException {
        java.util.List<String> images = new ArrayList<>();
        for (File imageFile : imageFiles) {
            images.add(Base64.getEncoder().encodeToString(Files.readAllBytes(imageFile.toPath())));
        }
        this.request.setImages(images);
        return this;
    }
}
