package io.github.ollama4j.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import static io.github.ollama4j.utils.Utils.generateJsonSchema;

public class ResponseClassToJsonSchemaSerializer extends JsonSerializer<Class<?>>{

    @Override
    public void serialize(Class<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        JsonNode schema = generateJsonSchema(value);
        gen.writeTree(schema);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, Class<?> value){
        return value == null;
    }

}
