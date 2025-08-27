package io.github.ollama4j.tools.sampletools;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.ollama4j.tools.Tools;

public class WeatherTool {
        private String openWeatherMapAPIKey = null;

        public WeatherTool(String openWeatherMapAPIKey) {
                this.openWeatherMapAPIKey = openWeatherMapAPIKey;
        }

        public String getCurrentWeather(Map<String, Object> arguments) {
                String city = (String) arguments.get("cityName");
                System.out.println("Finding weather for city: " + city);

                String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric",
                                city,
                                this.openWeatherMapAPIKey);

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .build();
                try {
                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        if (response.statusCode() == 200) {
                                ObjectMapper mapper = new ObjectMapper();
                                JsonNode root = mapper.readTree(response.body());
                                JsonNode main = root.path("main");
                                double temperature = main.path("temp").asDouble();
                                String description = root.path("weather").get(0).path("description").asText();
                                return String.format("Weather in %s: %.1f°C, %s", city, temperature, description);
                        } else {
                                return "Could not retrieve weather data for " + city + ". Status code: "
                                                + response.statusCode();
                        }
                } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        return "Error retrieving weather data: " + e.getMessage();
                }
        }

        public Tools.ToolSpecification getSpecification() {
                return Tools.ToolSpecification.builder()
                                .functionName("weather-reporter")
                                .functionDescription(
                                                "You are a tool who simply finds the city name from the user's message input/query about weather.")
                                .toolFunction(this::getCurrentWeather)
                                .toolPrompt(
                                                Tools.PromptFuncDefinition.builder()
                                                                .type("prompt")
                                                                .function(
                                                                                Tools.PromptFuncDefinition.PromptFuncSpec
                                                                                                .builder()
                                                                                                .name("get-city-name")
                                                                                                .description("Get the city name")
                                                                                                .parameters(
                                                                                                                Tools.PromptFuncDefinition.Parameters
                                                                                                                                .builder()
                                                                                                                                .type("object")
                                                                                                                                .properties(
                                                                                                                                                Map.of(
                                                                                                                                                                "cityName",
                                                                                                                                                                Tools.PromptFuncDefinition.Property
                                                                                                                                                                                .builder()
                                                                                                                                                                                .type("string")
                                                                                                                                                                                .description(
                                                                                                                                                                                                "The name of the city. e.g. Bengaluru")
                                                                                                                                                                                .required(true)
                                                                                                                                                                                .build()))
                                                                                                                                .required(java.util.List
                                                                                                                                                .of("cityName"))
                                                                                                                                .build())
                                                                                                .build())
                                                                .build())
                                .build();
        }
}
