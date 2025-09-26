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
 * Annotation to mark a method as a tool that can be registered automatically by
 * {@link OllamaAPI#registerAnnotatedTools()}.
 * <p>
 * Methods annotated with {@code @ToolSpec} will be discovered and registered as tools
 * when the containing class is specified as a provider in {@link OllamaToolService}.
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ToolSpec {

    /**
     * Specifies the name of the tool as exposed to the LLM.
     * If left empty, the method's name will be used as the tool name.
     *
     * @return the tool name
     */
    String name() default "";

    /**
     * Provides a detailed description of the tool's functionality.
     * This description is used by the LLM to determine when to call the tool.
     *
     * @return the tool description
     */
    String desc();
}
