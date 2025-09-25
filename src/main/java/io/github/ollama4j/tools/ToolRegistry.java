/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.tools;

import io.github.ollama4j.exceptions.ToolNotFoundException;
import java.util.*;

public class ToolRegistry {
    private final List<Tools.Tool> tools = new ArrayList<>();

    public ToolFunction getToolFunction(String name) throws ToolNotFoundException {
        for (Tools.Tool tool : tools) {
            if (tool.getToolSpec().getName().equals(name)) {
                return tool.getToolFunction();
            }
        }
        throw new ToolNotFoundException(String.format("Tool '%s' not found.", name));
    }

    public void addTool(Tools.Tool tool) {
        try {
            getToolFunction(tool.getToolSpec().getName());
        } catch (ToolNotFoundException e) {
            tools.add(tool);
        }
    }

    public void addTools(List<Tools.Tool> tools) {
        for (Tools.Tool tool : tools) {
            addTool(tool);
        }
    }

    public List<Tools.Tool> getRegisteredTools() {
        return tools;
    }

    /** Removes all registered tools from the registry. */
    public void clear() {
        tools.clear();
    }
}
