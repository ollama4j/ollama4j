package io.github.amithkoujalgi.ollama4j.core.models;

import static io.github.amithkoujalgi.ollama4j.core.utils.Utils.getObjectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.github.amithkoujalgi.ollama4j.core.utils.OllamaRequestBody;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class OllamaRequestModel implements OllamaRequestBody{

  private String model;
  private String prompt;
  private List<String> images;
  private Map<String, Object> options;

  public OllamaRequestModel(String model, String prompt) {
    this.model = model;
    this.prompt = prompt;
  }

  public OllamaRequestModel(String model, String prompt, List<String> images) {
    this.model = model;
    this.prompt = prompt;
    this.images = images;
  }

  public String toString() {
    try {
      return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
