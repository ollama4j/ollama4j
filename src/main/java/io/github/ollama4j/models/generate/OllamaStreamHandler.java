package io.github.ollama4j.models.generate;

import java.util.function.Consumer;

public interface OllamaStreamHandler extends Consumer<String> {
    void accept(String message);
}
