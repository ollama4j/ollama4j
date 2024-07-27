package io.github.ollama4j.models.generate;


import io.github.ollama4j.models.request.OllamaCommonRequest;
import io.github.ollama4j.utils.OllamaRequestBody;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OllamaGenerateRequest extends OllamaCommonRequest implements OllamaRequestBody{

  private String prompt;
  private List<String> images;

  private String system;
  private String context;
  private boolean raw;

  public OllamaGenerateRequest() {
  }

  public OllamaGenerateRequest(String model, String prompt) {
    this.model = model;
    this.prompt = prompt;
  }

  public OllamaGenerateRequest(String model, String prompt, List<String> images) {
    this.model = model;
    this.prompt = prompt;
    this.images = images;
  }

    @Override
  public boolean equals(Object o) {
    if (!(o instanceof OllamaGenerateRequest)) {
      return false;
    }

    return this.toString().equals(o.toString());
  }

}
