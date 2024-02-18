package io.github.amithkoujalgi.ollama4j.core.models;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.github.amithkoujalgi.ollama4j.core.utils.BooleanToJsonFormatFlagSerializer;
import io.github.amithkoujalgi.ollama4j.core.utils.Utils;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class OllamaCommonRequestModel {
  
  protected String model;  
  @JsonSerialize(using = BooleanToJsonFormatFlagSerializer.class)
  @JsonProperty(value = "format")
  protected Boolean returnFormatJson;
  protected Map<String, Object> options;
  protected String template;
  protected boolean stream;
  @JsonProperty(value = "keep_alive")
  protected String keepAlive;

  
  public String toString() {
    try {
      return Utils.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
