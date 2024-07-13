package io.github.amithkoujalgi.ollama4j.core.models.generate;

import java.util.function.Consumer;

public interface OllamaStreamHandler extends Consumer<String> {
    void accept(String message);
}
