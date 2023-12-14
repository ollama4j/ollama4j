package io.github.amithkoujalgi.ollama4j.core.models;

import static io.github.amithkoujalgi.ollama4j.core.utils.Utils.getObjectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;

/** The type Ollama result. */
@SuppressWarnings("unused")
public class OllamaResult {
  private final String response;

  private long responseTime = 0;

  public OllamaResult(String response, long responseTime) {
    this.response = response;
    this.responseTime = responseTime;
  }

  /**
   * Get the response text
   *
   * @return String - response text
   */
  public String getResponse() {
    return response;
  }

  /**
   * Get the response time in milliseconds.
   *
   * @return long - response time in milliseconds
   */
  public long getResponseTime() {
    return responseTime;
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
