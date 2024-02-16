package io.github.amithkoujalgi.ollama4j.core.models.generate;

import static io.github.amithkoujalgi.ollama4j.core.utils.Utils.getObjectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.github.amithkoujalgi.ollama4j.core.models.OllamaCommonRequestModel;
import io.github.amithkoujalgi.ollama4j.core.utils.OllamaRequestBody;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class OllamaGenerateRequestModel extends OllamaCommonRequestModel implements OllamaRequestBody{

  @NonNull
  private String prompt;
  private List<String> images;

  private String system;
  private String context;
  private boolean raw;


  public OllamaGenerateRequestModel(String model, String prompt) {
    super(model);
    this.prompt = prompt;
  }

  public OllamaGenerateRequestModel(String model, String prompt, List<String> images) {
    super(model);
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
