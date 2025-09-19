/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.impl;

import io.github.ollama4j.models.generate.OllamaGenerateTokenHandler;

public class ConsoleOutputGenerateTokenHandler implements OllamaGenerateTokenHandler {
    @Override
    public void accept(String message) {
        System.out.print(message);
    }
}
