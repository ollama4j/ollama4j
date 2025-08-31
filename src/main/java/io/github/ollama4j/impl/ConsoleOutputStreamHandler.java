package io.github.ollama4j.impl;

import io.github.ollama4j.models.generate.OllamaStreamHandler;

public class ConsoleOutputStreamHandler implements OllamaStreamHandler {
    @Override
    public void accept(String message) {
        System.out.print(message);
    }
}
