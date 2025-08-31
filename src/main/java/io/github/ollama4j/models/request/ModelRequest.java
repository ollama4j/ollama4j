package io.github.ollama4j.models.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Data;

import static io.github.ollama4j.utils.Utils.getObjectMapper;

@Data
@AllArgsConstructor
public class ModelRequest {
  private String name;

  @Override
  public String toString() {
    try {
      return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
