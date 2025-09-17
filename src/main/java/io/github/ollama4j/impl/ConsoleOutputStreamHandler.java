/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
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
