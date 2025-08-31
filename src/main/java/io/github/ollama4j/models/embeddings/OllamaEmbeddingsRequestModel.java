package io.github.ollama4j.models.embeddings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.github.ollama4j.utils.Utils.getObjectMapper;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Deprecated(since="1.0.90")
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
