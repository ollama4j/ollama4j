/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Base64;
import java.util.Collection;

public class FileToBase64Serializer extends JsonSerializer<Collection<byte[]>> {

    @Override
    public void serialize(
            Collection<byte[]> value, JsonGenerator jsonGenerator, SerializerProvider serializers)
            throws IOException {
        jsonGenerator.writeStartArray();
        for (byte[] file : value) {
            jsonGenerator.writeString(Base64.getEncoder().encodeToString(file));
        }
        jsonGenerator.writeEndArray();
    }
}
