/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.agent;

import io.github.ollama4j.exceptions.OllamaException;
import io.github.ollama4j.tools.ToolFunction;
import java.util.Map;

/** Example usage of the Agent API with some dummy tool functions. */
public class SampleAgent {
    public static void main(String[] args) throws OllamaException {
        Agent agent = Agent.fromYaml("agent.yaml");
        agent.runInteractive();
    }
}

/** ToolFunction implementation that returns a dummy weekly weather forecast. */
class WeatherToolFunction implements ToolFunction {
    @Override
    public Object apply(Map<String, Object> arguments) {
        String response =
                "Monday: Pleasant."
                        + "Tuesday: Sunny."
                        + "Wednesday: Windy."
                        + "Thursday: Cloudy."
                        + "Friday: Rainy."
                        + "Saturday: Heavy rains."
                        + "Sunday: Clear.";
        return response;
    }
}

/** ToolFunction implementation for basic arithmetic calculations. */
class CalculatorToolFunction implements ToolFunction {
    @Override
    public Object apply(Map<String, Object> arguments) {
        String operation = (String) arguments.get("operation");
        double a = ((Number) arguments.get("a")).doubleValue();
        double b = ((Number) arguments.get("b")).doubleValue();
        double result;
        switch (operation.toLowerCase()) {
            case "add":
                result = a + b;
                break;
            case "subtract":
                result = a - b;
                break;
            case "multiply":
                result = a * b;
                break;
            case "divide":
                if (b == 0) {
                    return "Cannot divide by zero.";
                }
                result = a / b;
                break;
            default:
                return "Unknown operation: " + operation;
        }
        return "Result: " + result;
    }
}

/** ToolFunction implementation simulating a hotel booking. */
class HotelBookingToolFunction implements ToolFunction {
    @Override
    public Object apply(Map<String, Object> arguments) {
        String city = (String) arguments.get("city");
        String checkin = (String) arguments.get("checkin_date");
        String checkout = (String) arguments.get("checkout_date");
        int guests = ((Number) arguments.get("guests")).intValue();

        // Dummy booking confirmation logic
        return String.format(
                "Booking confirmed! %d guest(s) in %s from %s to %s. (Confirmation #DUMMY1234)",
                guests, city, checkin, checkout);
    }
}
