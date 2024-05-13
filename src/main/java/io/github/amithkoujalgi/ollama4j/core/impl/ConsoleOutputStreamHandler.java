package io.github.amithkoujalgi.ollama4j.core.impl;

import io.github.amithkoujalgi.ollama4j.core.OllamaStreamHandler;

public class ConsoleOutputStreamHandler implements OllamaStreamHandler {
    private final StringBuffer response = new StringBuffer();

    @Override
    public void accept(String message) {
        String substr = message.substring(response.length());
        response.append(substr);
        System.out.print(substr);
    }
}
