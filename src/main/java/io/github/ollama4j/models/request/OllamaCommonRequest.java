package io.github.ollama4j.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.ollama4j.utils.BooleanToJsonFormatFlagSerializer;
import io.github.ollama4j.utils.Utils;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class OllamaCommonRequest {

  protected String model;
//  @JsonSerialize(using = BooleanToJsonFormatFlagSerializer.class)
//  this can either be set to format=json or format={"key1": "val1", "key2": "val2"}
  @JsonProperty(value = "format", required = false, defaultValue = "json")
  protected Object format;
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
