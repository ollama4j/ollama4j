/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.agent;

import io.github.ollama4j.Ollama;
import io.github.ollama4j.exceptions.OllamaException;
import io.github.ollama4j.tools.ToolFunction;
import io.github.ollama4j.tools.Tools;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SampleAgent {
    public static void main(String[] args) throws OllamaException {
        Ollama ollama = new Ollama("http://192.168.29.224:11434");
        ollama.setRequestTimeoutSeconds(120);
        String model = "mistral:7b";
        ollama.pullModel(model);

        List<Tools.Tool> tools = new ArrayList<>();
        // Weather tool
        tools.add(
                Tools.Tool.builder()
                        .toolSpec(
                                Tools.ToolSpec.builder()
                                        .name("weather-tool")
                                        .description(
                                                "Gets current weather for a given location and a"
                                                        + " given day")
                                        .parameters(
                                                Tools.Parameters.of(
                                                        Map.of(
                                                                "location",
                                                                Tools.Property.builder()
                                                                        .type("string")
                                                                        .description(
                                                                                "The location to"
                                                                                        + " get the"
                                                                                        + " weather"
                                                                                        + " for.")
                                                                        .required(true)
                                                                        .build(),
                                                                "day",
                                                                Tools.Property.builder()
                                                                        .type("string")
                                                                        .description(
                                                                                "The day of the"
                                                                                    + " week to get"
                                                                                    + " the weather"
                                                                                    + " for.")
                                                                        .required(true)
                                                                        .build())))
                                        .build())
                        .toolFunction(new WeatherToolFunction())
                        .build());

        // Calculator tool
        tools.add(
                Tools.Tool.builder()
                        .toolSpec(
                                Tools.ToolSpec.builder()
                                        .name("calculator-tool")
                                        .description(
                                                "Performs a simple arithmetic operation between two"
                                                        + " numbers")
                                        .parameters(
                                                Tools.Parameters.of(
                                                        Map.of(
                                                                "operation",
                                                                Tools.Property.builder()
                                                                        .type("string")
                                                                        .description(
                                                                                "Arithmetic"
                                                                                    + " operation"
                                                                                    + " to perform:"
                                                                                    + " add,"
                                                                                    + " subtract,"
                                                                                    + " multiply,"
                                                                                    + " divide")
                                                                        .required(true)
                                                                        .build(),
                                                                "a",
                                                                Tools.Property.builder()
                                                                        .type("number")
                                                                        .description(
                                                                                "The first operand")
                                                                        .required(true)
                                                                        .build(),
                                                                "b",
                                                                Tools.Property.builder()
                                                                        .type("number")
                                                                        .description(
                                                                                "The second"
                                                                                    + " operand")
                                                                        .required(true)
                                                                        .build())))
                                        .build())
                        .toolFunction(new CalculatorToolFunction())
                        .build());

        // Hotel Booking tool (dummy)
        tools.add(
                Tools.Tool.builder()
                        .toolSpec(
                                Tools.ToolSpec.builder()
                                        .name("hotel-booking-tool")
                                        .description(
                                                "Helps with booking a hotel room in a specified"
                                                    + " city for given dates and number of guests.")
                                        .parameters(
                                                Tools.Parameters.of(
                                                        Map.of(
                                                                "city",
                                                                Tools.Property.builder()
                                                                        .type("string")
                                                                        .description(
                                                                                "The city where you"
                                                                                    + " want to"
                                                                                    + " book the"
                                                                                    + " hotel.")
                                                                        .required(true)
                                                                        .build(),
                                                                "checkin_date",
                                                                Tools.Property.builder()
                                                                        .type("string")
                                                                        .description(
                                                                                "Check-in date.")
                                                                        .required(true)
                                                                        .build(),
                                                                "checkout_date",
                                                                Tools.Property.builder()
                                                                        .type("string")
                                                                        .description(
                                                                                "Check-out date.")
                                                                        .required(true)
                                                                        .build(),
                                                                "guests",
                                                                Tools.Property.builder()
                                                                        .type("number")
                                                                        .description(
                                                                                "Number of guests.")
                                                                        .required(true)
                                                                        .build())))
                                        .build())
                        .toolFunction(new HotelBookingToolFunction())
                        .build());

        Agent agent = new Agent("Nimma Mirta", ollama, model, tools);
        agent.runInteractive();
    }
}

/** ToolFunction implementation for diff checking. */
class WeatherToolFunction implements ToolFunction {
    @Override
    public Object apply(Map<String, Object> arguments) {
        String response =
                "Monday: pleasant, Tuesday: Sunny, Wednesday: Windy, Thursday: Cloudy, Friday:"
                        + " Rainy, Saturday: Heavy rains, Sunday: Clear";
        return response;
    }
}

/** ToolFunction implementation for simple calculations. */
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

/** ToolFunction implementation for a dummy hotel booking agent. */
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
