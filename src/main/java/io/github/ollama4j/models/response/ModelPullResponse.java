package io.github.ollama4j.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelPullResponse {
    private String status;
    private String digest;
    private long total;
    private long completed;
}
