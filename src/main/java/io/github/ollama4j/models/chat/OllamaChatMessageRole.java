package io.github.ollama4j.models.chat;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines the possible Chat Message roles.
 */
public enum OllamaChatMessageRole {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant"),
    TOOL("tool");

    @JsonValue
    private String roleName;

    private OllamaChatMessageRole(String roleName){
        this.roleName = roleName;
    }
}
