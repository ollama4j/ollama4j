/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.chat;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.ollama4j.exceptions.RoleNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * Defines the possible Chat Message roles.
 */
@Getter
public class OllamaChatMessageRole {
    private static final List<OllamaChatMessageRole> roles = new ArrayList<>();

    public static final OllamaChatMessageRole SYSTEM = new OllamaChatMessageRole("system");
    public static final OllamaChatMessageRole USER = new OllamaChatMessageRole("user");
    public static final OllamaChatMessageRole ASSISTANT = new OllamaChatMessageRole("assistant");
    public static final OllamaChatMessageRole TOOL = new OllamaChatMessageRole("tool");

    @JsonValue private final String roleName;

    private OllamaChatMessageRole(String roleName) {
        this.roleName = roleName;
        roles.add(this);
    }

    public static OllamaChatMessageRole newCustomRole(String roleName) {
        return new OllamaChatMessageRole(roleName);
    }

    public static List<OllamaChatMessageRole> getRoles() {
        return new ArrayList<>(roles);
    }

    public static OllamaChatMessageRole getRole(String roleName) throws RoleNotFoundException {
        for (OllamaChatMessageRole role : roles) {
            if (role.roleName.equals(roleName)) {
                return role;
            }
        }
        throw new RoleNotFoundException("Invalid role name: " + roleName);
    }

    @Override
    public String toString() {
        return roleName;
    }
}
