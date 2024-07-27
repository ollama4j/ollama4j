package io.github.ollama4j.utils;

import java.io.IOException;
import java.util.Base64;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class FileToBase64Serializer extends JsonSerializer<Collection<byte[]>> {

    @Override
    public void serialize(Collection<byte[]> value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        jsonGenerator.writeStartArray();
        for (byte[] file : value) {
            jsonGenerator.writeString(Base64.getEncoder().encodeToString(file));
        }
        jsonGenerator.writeEndArray();
    }
}