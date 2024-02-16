package io.github.amithkoujalgi.ollama4j.core.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class BooleanToJsonFormatFlagSerializer extends JsonSerializer<Boolean>{

    @Override
    public void serialize(Boolean value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if(value){
            gen.writeString("json");
        }
    }

}
