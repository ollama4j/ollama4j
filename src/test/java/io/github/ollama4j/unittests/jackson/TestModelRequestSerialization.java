/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests.jackson;

import static org.junit.jupiter.api.Assertions.*;

import io.github.ollama4j.models.response.Model;
import org.junit.jupiter.api.Test;

public class TestModelRequestSerialization extends AbstractSerializationTest<Model> {

    @Test
    public void testDeserializationOfModelResponseWithOffsetTime() {
        String serializedTestStringWithOffsetTime =
                "{\n"
                        + "  \"name\": \"codellama:13b\",\n"
                        + "  \"modified_at\": \"2023-11-04T14:56:49.277302595-07:00\",\n"
                        + "  \"size\": 7365960935,\n"
                        + "  \"digest\":"
                        + " \"9f438cb9cd581fc025612d27f7c1a6669ff83a8bb0ed86c94fcf4c5440555697\",\n"
                        + "  \"details\": {\n"
                        + "    \"format\": \"gguf\",\n"
                        + "    \"family\": \"llama\",\n"
                        + "    \"families\": null,\n"
                        + "    \"parameter_size\": \"13B\",\n"
                        + "    \"quantization_level\": \"Q4_0\"\n"
                        + "  }\n"
                        + "}";
        Model model = deserialize(serializedTestStringWithOffsetTime, Model.class);
        assertNotNull(model);
        assertEquals("codellama:13b", model.getName());
        assertEquals("2023-11-04T21:56:49.277302595Z", model.getModifiedAt().toString());
        assertEquals(7365960935L, model.getSize());
        assertEquals(
                "9f438cb9cd581fc025612d27f7c1a6669ff83a8bb0ed86c94fcf4c5440555697",
                model.getDigest());
        assertNotNull(model.getModelMeta());
        assertEquals("gguf", model.getModelMeta().getFormat());
        assertEquals("llama", model.getModelMeta().getFamily());
        assertNull(model.getModelMeta().getFamilies());
        assertEquals("13B", model.getModelMeta().getParameterSize());
        assertEquals("Q4_0", model.getModelMeta().getQuantizationLevel());
    }

    @Test
    public void testDeserializationOfModelResponseWithZuluTime() {
        String serializedTestStringWithZuluTimezone =
                "{\n"
                        + "  \"name\": \"codellama:13b\",\n"
                        + "  \"modified_at\": \"2023-11-04T14:56:49.277302595Z\",\n"
                        + "  \"size\": 7365960935,\n"
                        + "  \"digest\":"
                        + " \"9f438cb9cd581fc025612d27f7c1a6669ff83a8bb0ed86c94fcf4c5440555697\",\n"
                        + "  \"details\": {\n"
                        + "    \"format\": \"gguf\",\n"
                        + "    \"family\": \"llama\",\n"
                        + "    \"families\": null,\n"
                        + "    \"parameter_size\": \"13B\",\n"
                        + "    \"quantization_level\": \"Q4_0\"\n"
                        + "  }\n"
                        + "}";
        Model model = deserialize(serializedTestStringWithZuluTimezone, Model.class);
        assertNotNull(model);
        assertEquals("codellama:13b", model.getName());
        assertEquals("2023-11-04T14:56:49.277302595Z", model.getModifiedAt().toString());
        assertEquals(7365960935L, model.getSize());
        assertEquals(
                "9f438cb9cd581fc025612d27f7c1a6669ff83a8bb0ed86c94fcf4c5440555697",
                model.getDigest());
        assertNotNull(model.getModelMeta());
        assertEquals("gguf", model.getModelMeta().getFormat());
        assertEquals("llama", model.getModelMeta().getFamily());
        assertNull(model.getModelMeta().getFamilies());
        assertEquals("13B", model.getModelMeta().getParameterSize());
        assertEquals("Q4_0", model.getModelMeta().getQuantizationLevel());
    }
}
