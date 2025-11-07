/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.chat;

import io.github.ollama4j.models.request.OllamaCommonRequest;
import io.github.ollama4j.models.request.ThinkMode;
import io.github.ollama4j.models.request.ThinkModeSerializer;
import io.github.ollama4j.tools.Tools;
import io.github.ollama4j.utils.OllamaRequestBody;
import io.github.ollama4j.utils.Options;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Defines a Request to use against the ollama /api/chat endpoint.
 *
 * @see <a href=
 *     "https://github.com/ollama/ollama/blob/main/docs/api.md#generate-a-chat-completion">Generate
 *     Chat Completion</a>
 */
@Getter
@Setter
public class OllamaChatRequest extends OllamaCommonRequest implements OllamaRequestBody {

    private List<OllamaChatMessage> messages = new ArrayList<>();

    private List<Tools.Tool> tools = new ArrayList<>();

    @com.fasterxml.jackson.databind.annotation.JsonSerialize(using = ThinkModeSerializer.class)
    private ThinkMode think;

    /**
     * Controls whether tools are automatically executed.
     *
     * <p>If set to {@code true} (the default), tools will be automatically used/applied by the
     * library. If set to {@code false}, tool calls will be returned to the client for manual
     * handling.
     *
     * <p>Disabling this should be an explicit operation.
     */
    private boolean useTools = true;

    public OllamaChatRequest() {}

    public OllamaChatRequest(String model, ThinkMode think, List<OllamaChatMessage> messages) {
        this.model = model;
        this.messages = messages;
        this.think = think;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OllamaChatRequest)) {
            return false;
        }
        return this.toString().equals(o.toString());
    }

    // --- Builder-like fluent API methods ---

    public static OllamaChatRequest builder() {
        OllamaChatRequest req = new OllamaChatRequest();
        req.setMessages(new ArrayList<>());
        return req;
    }

    public OllamaChatRequest withModel(String model) {
        this.setModel(model);
        return this;
    }

    public OllamaChatRequest withMessage(OllamaChatMessageRole role, String content) {
        return withMessage(role, content, new ArrayList<>());
    }

    public OllamaChatRequest withMessage(
            OllamaChatMessageRole role, String content, List<OllamaChatToolCalls> toolCalls) {
        if (this.messages == null || this.messages == Collections.EMPTY_LIST) {
            this.messages = new ArrayList<>();
        }
        this.messages.add(new OllamaChatMessage(role, content, null, toolCalls, null));
        return this;
    }

    public OllamaChatRequest withMessage(
            OllamaChatMessageRole role,
            String content,
            List<OllamaChatToolCalls> toolCalls,
            List<File> images) {
        if (this.messages == null || this.messages == Collections.EMPTY_LIST) {
            this.messages = new ArrayList<>();
        }

        List<byte[]> imagesAsBytes = new ArrayList<>();
        if (images != null) {
            for (File image : images) {
                try {
                    imagesAsBytes.add(java.nio.file.Files.readAllBytes(image.toPath()));
                } catch (java.io.IOException e) {
                    throw new RuntimeException(
                            "Failed to read image file: " + image.getAbsolutePath(), e);
                }
            }
        }
        this.messages.add(new OllamaChatMessage(role, content, null, toolCalls, imagesAsBytes));
        return this;
    }

    public OllamaChatRequest withMessages(List<OllamaChatMessage> messages) {
        this.setMessages(messages);
        return this;
    }

    public OllamaChatRequest withOptions(Options options) {
        if (options != null) {
            this.setOptions(options.getOptionsMap());
        }
        return this;
    }

    public OllamaChatRequest withGetJsonResponse() {
        this.setFormat("json");
        return this;
    }

    public OllamaChatRequest withTemplate(String template) {
        this.setTemplate(template);
        return this;
    }

    public OllamaChatRequest withStreaming() {
        this.setStream(true);
        return this;
    }

    public OllamaChatRequest withKeepAlive(String keepAlive) {
        this.setKeepAlive(keepAlive);
        return this;
    }

    public OllamaChatRequest withThinking(ThinkMode think) {
        this.setThink(think);
        return this;
    }

    public OllamaChatRequest withUseTools(boolean useTools) {
        this.setUseTools(useTools);
        return this;
    }

    public OllamaChatRequest withTools(List<Tools.Tool> tools) {
        this.setTools(tools);
        return this;
    }

    public OllamaChatRequest build() {
        return this;
    }

    public void reset() {
        // Only clear the messages, keep model and think as is
        if (this.messages == null || this.messages == Collections.EMPTY_LIST) {
            this.messages = new ArrayList<>();
        } else {
            this.messages.clear();
        }
    }
}
