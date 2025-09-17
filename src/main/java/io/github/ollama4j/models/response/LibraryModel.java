/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class LibraryModel {

    private String name;
    private String description;
    private String pullCount;
    private int totalTags;
    private List<String> popularTags = new ArrayList<>();
    private String lastUpdated;
}
