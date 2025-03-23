package io.github.ollama4j.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Structured response for Ollama API
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OllamaStructuredResult {

    @JsonProperty("response")
    private String response;

    @JsonProperty("httpStatusCode")
    private int httpStatusCode;

    @JsonProperty("responseTime")
    private long responseTime;
}
