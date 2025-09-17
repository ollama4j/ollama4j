/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.response;

import lombok.Data;

@Data
public class LibraryModelTag {
    private String name;
    private String tag;
    private String size;
    private String lastUpdated;
}
