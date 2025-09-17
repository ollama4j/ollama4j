/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.samples;

import io.github.ollama4j.tools.annotations.ToolProperty;
import io.github.ollama4j.tools.annotations.ToolSpec;
import java.math.BigDecimal;

public class AnnotatedTool {

    @ToolSpec(desc = "Computes the most important constant all around the globe!")
    public String computeImportantConstant(
            @ToolProperty(name = "noOfDigits", desc = "Number of digits that shall be returned")
                    Integer noOfDigits) {
        return BigDecimal.valueOf((long) (Math.random() * 1000000L), noOfDigits).toString();
    }

    @ToolSpec(desc = "Says hello to a friend!")
    public String sayHello(
            @ToolProperty(name = "name", desc = "Name of the friend") String name,
            @ToolProperty(
                            name = "numberOfHearts",
                            desc = "number of heart emojis that should be used",
                            required = false)
                    Integer numberOfHearts) {
        String hearts = numberOfHearts != null ? "♡".repeat(numberOfHearts) : "";
        return "Hello, " + name + "! " + hearts;
    }
}
