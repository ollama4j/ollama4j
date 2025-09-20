/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.tools;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Specification of a {@link ToolFunction} that provides the implementation via java reflection calling.
 */
@Setter
@Getter
@AllArgsConstructor
public class ReflectionalToolFunction implements ToolFunction {

    private Object functionHolder;
    private Method function;
    private LinkedHashMap<String, String> propertyDefinition;

    @Override
    public Object apply(Map<String, Object> arguments) {
        LinkedHashMap<String, Object> argumentsCopy = new LinkedHashMap<>(this.propertyDefinition);
        for (Map.Entry<String, String> param : this.propertyDefinition.entrySet()) {
            argumentsCopy.replace(
                    param.getKey(), typeCast(arguments.get(param.getKey()), param.getValue()));
        }
        try {
            return function.invoke(functionHolder, argumentsCopy.values().toArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke tool: " + function.getName(), e);
        }
    }

    private Object typeCast(Object inputValue, String className) {
        if (className == null || inputValue == null) {
            return null;
        }
        String inputValueString = inputValue.toString();
        switch (className) {
            case "java.lang.Integer":
                return Integer.parseInt(inputValueString);
            case "java.lang.Boolean":
                return Boolean.valueOf(inputValueString);
            case "java.math.BigDecimal":
                return new BigDecimal(inputValueString);
            default:
                return inputValueString;
        }
    }
}
