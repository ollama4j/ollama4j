package io.github.ollama4j.unittests;

import io.github.ollama4j.tools.ToolFunction;
import io.github.ollama4j.tools.ToolRegistry;
import io.github.ollama4j.tools.Tools;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestToolRegistry {

    @Test
    public void testAddAndGetToolFunction() {
        ToolRegistry registry = new ToolRegistry();
        ToolFunction fn = args -> "ok:" + args.get("x");

        Tools.ToolSpecification spec = Tools.ToolSpecification.builder()
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
    public void testGetUnknownReturnsNull() {
        ToolRegistry registry = new ToolRegistry();
        assertNull(registry.getToolFunction("nope"));
    }

    @Test
    public void testClearRemovesAll() {
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
