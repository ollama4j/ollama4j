package io.github.ollama4j.models.request;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.ollama4j.utils.ResponseClassToJsonSchemaSerializer;
import io.github.ollama4j.utils.Utils;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class OllamaCommonRequest {

  protected String model;
  protected Map<String, Object> options;
  protected String template;
  protected boolean stream;
  @JsonProperty(value = "keep_alive")
  protected String keepAlive;
  @JsonSerialize(using = ResponseClassToJsonSchemaSerializer.class)
  @JsonProperty(value = "format", access = JsonProperty.Access.READ_ONLY)
  protected Class<?> responseClass;

  
  public String toString() {
    try {
      return Utils.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
