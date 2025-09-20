/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.ollama4j.utils.FileToBase64Serializer;
import io.github.ollama4j.utils.Utils;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TestFileToBase64Serializer {

    static class Holder {
        @JsonSerialize(using = FileToBase64Serializer.class)
        public List<byte[]> images;
    }

    @Test
    public void testSerializeByteArraysToBase64Array() throws JsonProcessingException {
        ObjectMapper mapper = Utils.getObjectMapper();

        Holder holder = new Holder();
        holder.images = List.of("hello".getBytes(), "world".getBytes());

        String json = mapper.writeValueAsString(holder);
        // Base64 of "hello" = aGVsbG8=, of "world" = d29ybGQ=
        assertEquals("{\"images\":[\"aGVsbG8=\",\"d29ybGQ=\"]}", json);
    }
}
