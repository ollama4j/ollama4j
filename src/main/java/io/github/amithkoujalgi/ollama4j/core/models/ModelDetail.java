package io.github.amithkoujalgi.ollama4j.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelDetail {
  private String license;

  @JsonProperty("modelfile")
  private String modelFile;

  private String parameters;
  private String template;
  private String system;
  private Map<String, String> details;
}
