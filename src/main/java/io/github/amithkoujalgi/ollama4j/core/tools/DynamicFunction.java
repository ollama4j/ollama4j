package io.github.amithkoujalgi.ollama4j.core.tools;

import java.util.Map;

@FunctionalInterface
public interface DynamicFunction {
    Object apply(Map<String, Object> arguments);
}
