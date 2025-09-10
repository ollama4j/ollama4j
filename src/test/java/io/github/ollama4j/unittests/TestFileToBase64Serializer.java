package io.github.ollama4j.unittests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.ollama4j.utils.FileToBase64Serializer;
import io.github.ollama4j.utils.Utils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
