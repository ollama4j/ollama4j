package io.github.amithkoujalgi.ollama4j.core.tools;

import java.util.Map;

@FunctionalInterface
public interface ToolFunction {
    Object apply(Map<String, Object> arguments);
}
