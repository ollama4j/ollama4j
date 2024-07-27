package io.github.ollama4j.impl;

import io.github.ollama4j.models.generate.OllamaStreamHandler;

public class ConsoleOutputStreamHandler implements OllamaStreamHandler {
    private final StringBuffer response = new StringBuffer();

    @Override
    public void accept(String message) {
        String substr = message.substring(response.length());
        response.append(substr);
        System.out.print(substr);
    }
}
