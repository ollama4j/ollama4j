/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests;

import static org.junit.jupiter.api.Assertions.*;

import io.github.ollama4j.exceptions.RoleNotFoundException;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import java.util.List;
import org.junit.jupiter.api.Test;

class TestOllamaChatMessageRole {

    @Test
    void testStaticRolesRegistered() throws Exception {
        List<OllamaChatMessageRole> roles = OllamaChatMessageRole.getRoles();
        assertTrue(roles.contains(OllamaChatMessageRole.SYSTEM));
        assertTrue(roles.contains(OllamaChatMessageRole.USER));
        assertTrue(roles.contains(OllamaChatMessageRole.ASSISTANT));
        assertTrue(roles.contains(OllamaChatMessageRole.TOOL));

        assertEquals("system", OllamaChatMessageRole.SYSTEM.toString());
        assertEquals("user", OllamaChatMessageRole.USER.toString());
        assertEquals("assistant", OllamaChatMessageRole.ASSISTANT.toString());
        assertEquals("tool", OllamaChatMessageRole.TOOL.toString());

        assertSame(OllamaChatMessageRole.SYSTEM, OllamaChatMessageRole.getRole("system"));
        assertSame(OllamaChatMessageRole.USER, OllamaChatMessageRole.getRole("user"));
        assertSame(OllamaChatMessageRole.ASSISTANT, OllamaChatMessageRole.getRole("assistant"));
        assertSame(OllamaChatMessageRole.TOOL, OllamaChatMessageRole.getRole("tool"));
    }

    @Test
    void testCustomRoleCreationAndLookup() throws Exception {
        OllamaChatMessageRole custom = OllamaChatMessageRole.newCustomRole("myrole");
        assertEquals("myrole", custom.toString());
        // custom roles are registered globally (per current implementation), so lookup should
        // succeed
        assertSame(custom, OllamaChatMessageRole.getRole("myrole"));
    }

    @Test
    void testGetRoleThrowsOnUnknown() {
        assertThrows(
                RoleNotFoundException.class, () -> OllamaChatMessageRole.getRole("does-not-exist"));
    }
}
