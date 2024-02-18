package io.github.amithkoujalgi.ollama4j.core.models.generate;


import io.github.amithkoujalgi.ollama4j.core.models.OllamaCommonRequestModel;
import io.github.amithkoujalgi.ollama4j.core.utils.OllamaRequestBody;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OllamaGenerateRequestModel extends OllamaCommonRequestModel implements OllamaRequestBody{

  private String prompt;
  private List<String> images;

  private String system;
  private String context;
  private boolean raw;

  public OllamaGenerateRequestModel() {
  }

  public OllamaGenerateRequestModel(String model, String prompt) {
    this.model = model;
    this.prompt = prompt;
  }

  public OllamaGenerateRequestModel(String model, String prompt, List<String> images) {
    this.model = model;
    this.prompt = prompt;
    this.images = images;
  }

    @Override
  public boolean equals(Object o) {
    if (!(o instanceof OllamaGenerateRequestModel)) {
      return false;
    }

    return this.toString().equals(o.toString());
  }

}
