/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.generate;

import io.github.ollama4j.utils.OllamaRequestBody;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OllamaGenerateImageRequest implements OllamaRequestBody {
    private String model;
    private String prompt;
    private Integer width;
    private Integer height;
}
