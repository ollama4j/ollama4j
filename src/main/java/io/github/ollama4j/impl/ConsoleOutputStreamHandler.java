package io.github.ollama4j.impl;

import io.github.ollama4j.models.generate.OllamaStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleOutputStreamHandler implements OllamaStreamHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ConsoleOutputStreamHandler.class);

    @Override
    public void accept(String message) {
        LOG.info(message);
    }
}
