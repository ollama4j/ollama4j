/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.tools.annotations;

import io.github.ollama4j.OllamaAPI;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a class as an Ollama tool service.
 * <p>
 * When a class is annotated with {@code @OllamaToolService}, the method
 * {@link OllamaAPI#registerAnnotatedTools()} can be used to automatically register all tool provider
 * classes specified in the {@link #providers()} array. All methods in those provider classes that are
 * annotated with {@link ToolSpec} will be registered as tools.
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OllamaToolService {

    /**
     * Specifies the provider classes whose methods annotated with {@link ToolSpec} should be registered as tools.
     * Each provider class must have a public no-argument constructor.
     *
     * @return an array of provider classes to be used for tool registration
     */
    Class<?>[] providers();
}
