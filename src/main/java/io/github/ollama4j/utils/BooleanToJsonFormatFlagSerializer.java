package io.github.ollama4j.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class BooleanToJsonFormatFlagSerializer extends JsonSerializer<Boolean>{

    @Override
    public void serialize(Boolean value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString("json");
    }

    @Override
    public boolean isEmpty(SerializerProvider provider,Boolean value){
        return !value;
    }

}
