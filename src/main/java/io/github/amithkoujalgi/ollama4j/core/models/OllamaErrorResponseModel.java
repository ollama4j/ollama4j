package io.github.amithkoujalgi.ollama4j.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OllamaErrorResponseModel {
  private String error;

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }
}
