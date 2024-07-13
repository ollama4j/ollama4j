package io.github.amithkoujalgi.ollama4j.core.models.embeddings;

import static io.github.amithkoujalgi.ollama4j.core.utils.Utils.getObjectMapper;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class OllamaEmbeddingsRequestModel {
  @NonNull
  private String model;
  @NonNull
  private String prompt;

  protected Map<String, Object> options;
  @JsonProperty(value = "keep_alive")
  private String keepAlive;

  @Override
  public String toString() {
    try {
      return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
