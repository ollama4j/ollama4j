package io.github.amithkoujalgi.ollama4j.core.tools;

import java.util.HashMap;
import java.util.Map;

public class ToolRegistry {
    private static final Map<String, DynamicFunction> functionMap = new HashMap<>();


    public static DynamicFunction getFunction(String name) {
        return functionMap.get(name);
    }

    public static void addFunction(String name, DynamicFunction function) {
        functionMap.put(name, function);
    }
}
