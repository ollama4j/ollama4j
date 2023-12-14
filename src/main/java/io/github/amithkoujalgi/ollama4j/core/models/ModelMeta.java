package io.github.amithkoujalgi.ollama4j.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelMeta {
  @JsonProperty("format")
  private String format;

  @JsonProperty("family")
  private String family;

  @JsonProperty("families")
  private String[] families;

  @JsonProperty("parameter_size")
  private String parameterSize;

  @JsonProperty("quantization_level")
  private String quantizationLevel;
}
