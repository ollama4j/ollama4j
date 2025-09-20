/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.tools.sampletools;

import io.github.ollama4j.tools.Tools;
import java.util.Map;

@SuppressWarnings("resource")
public class WeatherTool {
    private String paramCityName = "cityName";

    /**
     * Default constructor for WeatherTool.
     * This constructor is intentionally left empty because no initialization is required
     * for this sample tool. If future state or dependencies are needed, they can be added here.
     */
    public WeatherTool() {
        // No initialization required
    }

    public String getCurrentWeather(Map<String, Object> arguments) {
        String city = (String) arguments.get(paramCityName);
        return "It is sunny in " + city;
    }

    public Tools.ToolSpecification getSpecification() {
        return Tools.ToolSpecification.builder()
                .functionName("weather-reporter")
                .functionDescription(
                        "You are a tool who simply finds the city name from the user's message"
                                + " input/query about weather.")
                .toolFunction(this::getCurrentWeather)
                .toolPrompt(
                        Tools.PromptFuncDefinition.builder()
                                .type("prompt")
                                .function(
                                        Tools.PromptFuncDefinition.PromptFuncSpec.builder()
                                                .name("get-city-name")
                                                .description("Get the city name")
                                                .parameters(
                                                        Tools.PromptFuncDefinition.Parameters
                                                                .builder()
                                                                .type("object")
                                                                .properties(
                                                                        Map.of(
                                                                                paramCityName,
                                                                                Tools
                                                                                        .PromptFuncDefinition
                                                                                        .Property
                                                                                        .builder()
                                                                                        .type(
                                                                                                "string")
                                                                                        .description(
                                                                                                "The name"
                                                                                                    + " of the"
                                                                                                    + " city."
                                                                                                    + " e.g."
                                                                                                    + " Bengaluru")
                                                                                        .required(
                                                                                                true)
                                                                                        .build()))
                                                                .required(
                                                                        java.util.List.of(
                                                                                paramCityName))
                                                                .build())
                                                .build())
                                .build())
                .build();
    }
}
