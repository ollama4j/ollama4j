package io.github.ollama4j.models.request;

import static io.github.ollama4j.utils.Utils.getObjectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
@Builder
public class CustomModelRequest {
    private String model;
    private String from;
    private Map<String, String> files;
    private Map<String, String> adapters;
    private String template;
    private Object license; // Using Object to handle both String and List<String>
    private String system;
    private Map<String, Object> parameters;
    private List<Object> messages;
    private Boolean stream;
    private Boolean quantize;

    public CustomModelRequest() {
        this.stream = true;
        this.quantize = false;
    }

    @Override
    public String toString() {
        try {
            return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
