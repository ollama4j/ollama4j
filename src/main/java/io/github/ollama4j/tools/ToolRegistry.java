package io.github.ollama4j.tools;

import java.util.HashMap;
import java.util.Map;

public class ToolRegistry {
    private final Map<String, ToolFunction> functionMap = new HashMap<>();

    public ToolFunction getFunction(String name) {
        return functionMap.get(name);
    }

    public void addFunction(String name, ToolFunction function) {
        functionMap.put(name, function);
    }
}
