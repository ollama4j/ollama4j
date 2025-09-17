/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ToolRegistry {
    private final Map<String, Tools.ToolSpecification> tools = new HashMap<>();

    public ToolFunction getToolFunction(String name) {
        final Tools.ToolSpecification toolSpecification = tools.get(name);
        return toolSpecification != null ? toolSpecification.getToolFunction() : null;
    }

    public void addTool(String name, Tools.ToolSpecification specification) {
        tools.put(name, specification);
    }

    public Collection<Tools.ToolSpecification> getRegisteredSpecs() {
        return tools.values();
    }

    /**
     * Removes all registered tools from the registry.
     */
    public void clear() {
        tools.clear();
    }
}
