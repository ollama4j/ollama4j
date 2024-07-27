package io.github.ollama4j.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ollama4j.utils.Utils;
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

    @Override
  public String toString() {
    try {
      return Utils.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
