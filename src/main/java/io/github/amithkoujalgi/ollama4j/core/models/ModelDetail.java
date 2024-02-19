package io.github.amithkoujalgi.ollama4j.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.amithkoujalgi.ollama4j.core.utils.Utils;
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
  private ModelMeta details;

    @Override
  public String toString() {
    try {
      return Utils.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
