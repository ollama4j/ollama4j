package io.github.ollama4j.models.embeddings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static io.github.ollama4j.utils.Utils.getObjectMapper;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class OllamaEmbedRequestModel {
    @NonNull
    private String model;

    @NonNull
    private List<String> input;

    private Map<String, Object> options;

    @JsonProperty(value = "keep_alive")
    private String keepAlive;

    @JsonProperty(value = "truncate")
    private Boolean truncate = true;

    @Override
    public String toString() {
        try {
            return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
