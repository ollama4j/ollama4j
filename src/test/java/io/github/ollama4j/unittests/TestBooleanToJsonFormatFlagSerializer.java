package io.github.ollama4j.unittests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.ollama4j.utils.BooleanToJsonFormatFlagSerializer;
import io.github.ollama4j.utils.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestBooleanToJsonFormatFlagSerializer {

    static class Holder {
        @JsonSerialize(using = BooleanToJsonFormatFlagSerializer.class)
        public Boolean formatJson;
    }

    @Test
    void testSerializeTrueWritesJsonString() throws JsonProcessingException {
        ObjectMapper mapper = Utils.getObjectMapper().copy();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        Holder holder = new Holder();
        holder.formatJson = true;

        String json = mapper.writeValueAsString(holder);
        assertEquals("{\"formatJson\":\"json\"}", json);
    }

    @Test
    void testSerializeFalseOmittedByIsEmpty() throws JsonProcessingException {
        ObjectMapper mapper = Utils.getObjectMapper().copy();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        Holder holder = new Holder();
        holder.formatJson = false;

        String json = mapper.writeValueAsString(holder);
        assertEquals("{}", json);
    }
}
