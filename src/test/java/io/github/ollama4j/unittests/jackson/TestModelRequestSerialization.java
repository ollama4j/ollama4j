package io.github.ollama4j.unittests.jackson;

import io.github.ollama4j.models.response.Model;
import org.junit.jupiter.api.Test;

public class TestModelRequestSerialization extends AbstractSerializationTest<Model> {

    @Test
    public void testDeserializationOfModelResponseWithOffsetTime(){
        String serializedTestStringWithOffsetTime = "{\n"
                + "\"name\": \"codellama:13b\",\n"
                + "\"modified_at\": \"2023-11-04T14:56:49.277302595-07:00\",\n"
                + "\"size\": 7365960935,\n"
                + "\"digest\": \"9f438cb9cd581fc025612d27f7c1a6669ff83a8bb0ed86c94fcf4c5440555697\",\n"
                + "\"details\": {\n"
                + "\"format\": \"gguf\",\n"
                + "\"family\": \"llama\",\n"
                + "\"families\": null,\n"
                + "\"parameter_size\": \"13B\",\n"
                + "\"quantization_level\": \"Q4_0\"\n"
                + "}}";
        deserialize(serializedTestStringWithOffsetTime,Model.class);
    }

    @Test
    public void testDeserializationOfModelResponseWithZuluTime(){
        String serializedTestStringWithZuluTimezone = "{\n"
                + "\"name\": \"codellama:13b\",\n"
                + "\"modified_at\": \"2023-11-04T14:56:49.277302595Z\",\n"
                + "\"size\": 7365960935,\n"
                + "\"digest\": \"9f438cb9cd581fc025612d27f7c1a6669ff83a8bb0ed86c94fcf4c5440555697\",\n"
                + "\"details\": {\n"
                + "\"format\": \"gguf\",\n"
                + "\"family\": \"llama\",\n"
                + "\"families\": null,\n"
                + "\"parameter_size\": \"13B\",\n"
                + "\"quantization_level\": \"Q4_0\"\n"
                + "}}";
        deserialize(serializedTestStringWithZuluTimezone,Model.class);
    }

}
