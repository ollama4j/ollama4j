package io.github.amithkoujalgi.ollama4j.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ModelDetail {
  private String license;
  @JsonProperty("modelfile")
  private String modelFile;
  private String parameters;
  private String template;
  private String system;
}
