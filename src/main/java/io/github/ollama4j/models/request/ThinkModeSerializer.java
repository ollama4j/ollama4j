/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.request;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class ThinkModeSerializer extends JsonSerializer<ThinkMode> {
    @Override
    public void serialize(ThinkMode value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (value == null) {
            gen.writeBoolean(false);
        }
        if (value == ThinkMode.DISABLED || value == ThinkMode.ENABLED) {
            gen.writeBoolean((Boolean) value.getValue());
        } else {
            gen.writeString(value.getValue().toString());
        }
    }
}
