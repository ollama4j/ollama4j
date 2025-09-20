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

import io.github.ollama4j.tools.ToolFunction;
import io.github.ollama4j.tools.ToolRegistry;
import io.github.ollama4j.tools.Tools;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TestToolRegistry {

    @Test
    void testAddAndGetToolFunction() {
        ToolRegistry registry = new ToolRegistry();
        ToolFunction fn = args -> "ok:" + args.get("x");

        Tools.ToolSpecification spec =
                Tools.ToolSpecification.builder()
                        .functionName("test")
                        .functionDescription("desc")
                        .toolFunction(fn)
                        .build();

        registry.addTool("test", spec);
        ToolFunction retrieved = registry.getToolFunction("test");
        assertNotNull(retrieved);
        assertEquals("ok:42", retrieved.apply(Map.of("x", 42)));
    }

    @Test
    void testGetUnknownReturnsNull() {
        ToolRegistry registry = new ToolRegistry();
        assertNull(registry.getToolFunction("nope"));
    }

    @Test
    void testClearRemovesAll() {
        ToolRegistry registry = new ToolRegistry();
        registry.addTool("a", Tools.ToolSpecification.builder().toolFunction(args -> 1).build());
        registry.addTool("b", Tools.ToolSpecification.builder().toolFunction(args -> 2).build());
        assertFalse(registry.getRegisteredSpecs().isEmpty());
        registry.clear();
        assertTrue(registry.getRegisteredSpecs().isEmpty());
        assertNull(registry.getToolFunction("a"));
        assertNull(registry.getToolFunction("b"));
    }
}
