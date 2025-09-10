package io.github.ollama4j.unittests;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ollama4j.tools.Tools;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestToolsPromptBuilder {

    @Test
    void testPromptBuilderIncludesToolsAndPrompt() throws JsonProcessingException {
        Tools.PromptFuncDefinition.Property cityProp = Tools.PromptFuncDefinition.Property.builder()
                .type("string")
                .description("city name")
                .required(true)
                .build();

        Tools.PromptFuncDefinition.Property unitsProp = Tools.PromptFuncDefinition.Property.builder()
                .type("string")
                .description("units")
                .enumValues(List.of("metric", "imperial"))
                .required(false)
                .build();

        Tools.PromptFuncDefinition.Parameters params = Tools.PromptFuncDefinition.Parameters.builder()
                .type("object")
                .properties(Map.of("city", cityProp, "units", unitsProp))
                .build();

        Tools.PromptFuncDefinition.PromptFuncSpec spec = Tools.PromptFuncDefinition.PromptFuncSpec.builder()
                .name("getWeather")
                .description("Get weather for a city")
                .parameters(params)
                .build();

        Tools.PromptFuncDefinition def = Tools.PromptFuncDefinition.builder()
                .type("function")
                .function(spec)
                .build();

        Tools.ToolSpecification toolSpec = Tools.ToolSpecification.builder()
                .functionName("getWeather")
                .functionDescription("Get weather for a city")
                .toolPrompt(def)
                .build();

        Tools.PromptBuilder pb = new Tools.PromptBuilder()
                .withToolSpecification(toolSpec)
                .withPrompt("Tell me the weather.");

        String built = pb.build();
        assertTrue(built.contains("[AVAILABLE_TOOLS]"));
        assertTrue(built.contains("[/AVAILABLE_TOOLS]"));
        assertTrue(built.contains("[INST]"));
        assertTrue(built.contains("Tell me the weather."));
        assertTrue(built.contains("\"name\":\"getWeather\""));
        assertTrue(built.contains("\"required\":[\"city\"]"));
        assertTrue(built.contains("\"enum\":[\"metric\",\"imperial\"]"));
    }
}
