/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.generate;

import io.github.ollama4j.models.request.OllamaCommonRequest;
import io.github.ollama4j.models.request.ThinkMode;
import io.github.ollama4j.models.request.ThinkModeSerializer;
import io.github.ollama4j.tools.Tools;
import io.github.ollama4j.utils.OllamaRequestBody;
import io.github.ollama4j.utils.Options;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OllamaGenerateRequest extends OllamaCommonRequest implements OllamaRequestBody {

    private String prompt;
    private String suffix;
    private List<String> images;
    private String system;
    private String context;
    private boolean raw;

    @com.fasterxml.jackson.databind.annotation.JsonSerialize(using = ThinkModeSerializer.class)
    private ThinkMode think;

    private boolean useTools;
    private List<Tools.Tool> tools;

    public OllamaGenerateRequest() {}

    public OllamaGenerateRequest(String model, String prompt) {
        this.model = model;
        this.prompt = prompt;
    }

    public OllamaGenerateRequest(String model, String prompt, List<String> images) {
        this.model = model;
        this.prompt = prompt;
        this.images = images;
    }

    // --- Builder-style methods ---

    public static OllamaGenerateRequest builder() {
        return new OllamaGenerateRequest();
    }

    public OllamaGenerateRequest withPrompt(String prompt) {
        this.setPrompt(prompt);
        return this;
    }

    public OllamaGenerateRequest withSuffix(String suffix) {
        this.setSuffix(suffix);
        return this;
    }

    public OllamaGenerateRequest withTools(List<Tools.Tool> tools) {
        this.setTools(tools);
        return this;
    }

    public OllamaGenerateRequest withModel(String model) {
        this.setModel(model);
        return this;
    }

    public OllamaGenerateRequest withGetJsonResponse() {
        this.setFormat("json");
        return this;
    }

    public OllamaGenerateRequest withOptions(Options options) {
        this.setOptions(options.getOptionsMap());
        return this;
    }

    public OllamaGenerateRequest withTemplate(String template) {
        this.setTemplate(template);
        return this;
    }

    public OllamaGenerateRequest withStreaming(boolean streaming) {
        this.setStream(streaming);
        return this;
    }

    public OllamaGenerateRequest withKeepAlive(String keepAlive) {
        this.setKeepAlive(keepAlive);
        return this;
    }

    public OllamaGenerateRequest withRaw(boolean raw) {
        this.setRaw(raw);
        return this;
    }

    public OllamaGenerateRequest withThink(ThinkMode think) {
        this.setThink(think);
        return this;
    }

    public OllamaGenerateRequest withUseTools(boolean useTools) {
        this.setUseTools(useTools);
        return this;
    }

    public OllamaGenerateRequest withFormat(Map<String, Object> format) {
        this.setFormat(format);
        return this;
    }

    public OllamaGenerateRequest withSystem(String system) {
        this.setSystem(system);
        return this;
    }

    public OllamaGenerateRequest withContext(String context) {
        this.setContext(context);
        return this;
    }

    public OllamaGenerateRequest withImagesBase64(List<String> images) {
        this.setImages(images);
        return this;
    }

    public OllamaGenerateRequest withImages(List<File> imageFiles) throws IOException {
        List<String> images = new ArrayList<>();
        for (File imageFile : imageFiles) {
            images.add(Base64.getEncoder().encodeToString(Files.readAllBytes(imageFile.toPath())));
        }
        this.setImages(images);
        return this;
    }

    public OllamaGenerateRequest build() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OllamaGenerateRequest)) {
            return false;
        }
        return this.toString().equals(o.toString());
    }
}
