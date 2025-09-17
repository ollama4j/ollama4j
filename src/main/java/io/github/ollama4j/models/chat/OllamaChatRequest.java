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
import io.github.ollama4j.tools.Tools;
import io.github.ollama4j.utils.OllamaRequestBody;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Defines a Request to use against the ollama /api/chat endpoint.
 *
 * @see <a href=
 * "https://github.com/ollama/ollama/blob/main/docs/api.md#generate-a-chat-completion">Generate
 * Chat Completion</a>
 */
@Getter
@Setter
public class OllamaChatRequest extends OllamaCommonRequest implements OllamaRequestBody {

    private List<OllamaChatMessage> messages;

    private List<Tools.PromptFuncDefinition> tools;

    private boolean think;

    public OllamaChatRequest() {}

    public OllamaChatRequest(String model, boolean think, List<OllamaChatMessage> messages) {
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
}
