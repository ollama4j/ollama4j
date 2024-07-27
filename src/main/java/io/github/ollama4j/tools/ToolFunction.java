package io.github.ollama4j.tools;

import java.util.Map;

@FunctionalInterface
public interface ToolFunction {
    Object apply(Map<String, Object> arguments);
}
