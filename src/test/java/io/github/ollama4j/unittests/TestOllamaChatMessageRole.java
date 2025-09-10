package io.github.ollama4j.unittests;

import io.github.ollama4j.exceptions.RoleNotFoundException;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        // custom roles are registered globally (per current implementation), so lookup should succeed
        assertSame(custom, OllamaChatMessageRole.getRole("myrole"));
    }

    @Test
    void testGetRoleThrowsOnUnknown() {
        assertThrows(RoleNotFoundException.class, () -> OllamaChatMessageRole.getRole("does-not-exist"));
    }
}
