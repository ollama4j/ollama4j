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
        //        Ollama ollama = new Ollama("http://192.168.29.224:11434");
        //        ollama.setRequestTimeoutSeconds(120);
        //        String model = "mistral:7b";
        //        ollama.pullModel(model);
        //        List<Tools.Tool> tools = new ArrayList<>();
        //        // Weather tool
        //        tools.add(
        //                Tools.Tool.builder()
        //                        .toolSpec(
        //                                Tools.ToolSpec.builder()
        //                                        .name("weather-tool")
        //                                        .description(
        //                                                "Gets the current weather for a given
        // location and"
        //                                                        + " day.")
        //                                        .parameters(
        //                                                Tools.Parameters.of(
        //                                                        Map.of(
        //                                                                "location",
        //                                                                Tools.Property.builder()
        //                                                                        .type("string")
        //                                                                        .description(
        //                                                                                "The
        // location for"
        //                                                                                    + "
        // which to"
        //                                                                                    + "
        // get the"
        //                                                                                    + "
        // weather.")
        //                                                                        .required(true)
        //                                                                        .build(),
        //                                                                "day",
        //                                                                Tools.Property.builder()
        //                                                                        .type("string")
        //                                                                        .description(
        //                                                                                "The day
        // of the"
        //                                                                                    + "
        // week for"
        //                                                                                    + "
        // which to"
        //                                                                                    + "
        // get the"
        //                                                                                    + "
        // weather.")
        //                                                                        .required(true)
        //                                                                        .build())))
        //                                        .build())
        //                        .toolFunction(new WeatherToolFunction())
        //                        .build());
        //
        //        // Calculator tool
        //        tools.add(
        //                Tools.Tool.builder()
        //                        .toolSpec(
        //                                Tools.ToolSpec.builder()
        //                                        .name("calculator-tool")
        //                                        .description(
        //                                                "Performs a simple arithmetic operation
        // between two"
        //                                                        + " numbers.")
        //                                        .parameters(
        //                                                Tools.Parameters.of(
        //                                                        Map.of(
        //                                                                "operation",
        //                                                                Tools.Property.builder()
        //                                                                        .type("string")
        //                                                                        .description(
        //                                                                                "The
        // arithmetic"
        //                                                                                    + "
        // operation"
        //                                                                                    + " to
        // perform."
        //                                                                                    + "
        // One of:"
        //                                                                                    + "
        // add,"
        //                                                                                    + "
        // subtract,"
        //                                                                                    + "
        // multiply,"
        //                                                                                    + "
        // divide.")
        //                                                                        .required(true)
        //                                                                        .build(),
        //                                                                "a",
        //                                                                Tools.Property.builder()
        //                                                                        .type("number")
        //                                                                        .description(
        //                                                                                "The
        // first"
        //                                                                                    + "
        // operand.")
        //                                                                        .required(true)
        //                                                                        .build(),
        //                                                                "b",
        //                                                                Tools.Property.builder()
        //                                                                        .type("number")
        //                                                                        .description(
        //                                                                                "The
        // second"
        //                                                                                    + "
        // operand.")
        //                                                                        .required(true)
        //                                                                        .build())))
        //                                        .build())
        //                        .toolFunction(new CalculatorToolFunction())
        //                        .build());
        //
        //        // Hotel Booking tool (dummy)
        //        tools.add(
        //                Tools.Tool.builder()
        //                        .toolSpec(
        //                                Tools.ToolSpec.builder()
        //                                        .name("hotel-booking-tool")
        //                                        .description(
        //                                                "Books a hotel room in a specified city
        // for given"
        //                                                        + " dates and number of guests.")
        //                                        .parameters(
        //                                                Tools.Parameters.of(
        //                                                        Map.of(
        //                                                                "city",
        //                                                                Tools.Property.builder()
        //                                                                        .type("string")
        //                                                                        .description(
        //                                                                                "The city
        // where the"
        //                                                                                    + "
        // hotel will"
        //                                                                                    + " be
        // booked.")
        //                                                                        .required(true)
        //                                                                        .build(),
        //                                                                "checkin_date",
        //                                                                Tools.Property.builder()
        //                                                                        .type("string")
        //                                                                        .description(
        //                                                                                "Hotel
        // check-in date"
        //                                                                                    + "
        // (e.g."
        //                                                                                    + "
        // 2025-08-10).")
        //                                                                        .required(true)
        //                                                                        .build(),
        //                                                                "checkout_date",
        //                                                                Tools.Property.builder()
        //                                                                        .type("string")
        //                                                                        .description(
        //
        // "HotelCheck-out date"
        //                                                                                    + "
        // (e.g."
        //                                                                                    + "
        // 2025-08-12).")
        //                                                                        .required(true)
        //                                                                        .build(),
        //                                                                "guests",
        //                                                                Tools.Property.builder()
        //                                                                        .type("number")
        //                                                                        .description(
        //                                                                                "Number of
        // guests"
        //                                                                                    + "
        // for the"
        //                                                                                    + "
        // booking.")
        //                                                                        .required(true)
        //                                                                        .build())))
        //                                        .build())
        //                        .toolFunction(new HotelBookingToolFunction())
        //                        .build());
        //
        //        Map<String, ToolFunction> functionMap = Map.of(
        //                "weather-tool", new WeatherToolFunction(),
        //                "calculator-tool", new CalculatorToolFunction()
        //        );
        //        List<Tools.Tool> tools =
        // Tools.fromYAMLFile("/Users/amithkoujalgi/Downloads/tools.yaml", functionMap);
        //        Agent agent = new Agent("Nimma Mirta", ollama, model, tools);
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
