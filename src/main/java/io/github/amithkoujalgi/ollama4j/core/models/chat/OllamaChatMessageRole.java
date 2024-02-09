package io.github.amithkoujalgi.ollama4j.core.models.chat;

/**
 * Defines the possible Chat Message roles.
 */
public enum OllamaChatMessageRole {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assisstant");

    @SuppressWarnings("unused")
    private String roleName;

    private OllamaChatMessageRole(String roleName){
        this.roleName = roleName;
    }
}
