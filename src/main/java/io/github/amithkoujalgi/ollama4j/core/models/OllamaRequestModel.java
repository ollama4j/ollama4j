package io.github.amithkoujalgi.ollama4j.core.models;


import static io.github.amithkoujalgi.ollama4j.core.utils.Utils.getObjectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import lombok.Data;

@Data
public class OllamaRequestModel {

  private String model;
  private String prompt;
  private Options options;
  private List<String> images;

  public OllamaRequestModel(String model, String prompt) {
    this.model = model;
    this.prompt = prompt;
  }

  public OllamaRequestModel(String model, String prompt, List<String> images) {
    this.model = model;
    this.prompt = prompt;
    this.images = images;
  }

  /**
   * Set options
   */
  public void setOptions(Options options) {
    this.options = options;
  }

  @Override
  public String toString() {
    try {
      return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
