package io.github.amithkoujalgi.ollama4j.core.models;

import static io.github.amithkoujalgi.ollama4j.core.utils.Utils.getObjectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import lombok.Getter;

/** The type Ollama result. */
@Getter
@SuppressWarnings("unused")
@Data
public class OllamaResult {
  /**
   * -- GETTER --
   *  Get the response text
   *
   * @return String - response text
   */
  private final String response;

  /**
   * -- GETTER --
   *  Get the response status code.
   *
   * @return int - response status code
   */
  private int httpStatusCode;

  /**
   * -- GETTER --
   *  Get the response time in milliseconds.
   *
   * @return long - response time in milliseconds
   */
  private long responseTime = 0;

  public OllamaResult(String response, long responseTime, int httpStatusCode) {
    this.response = response;
    this.responseTime = responseTime;
    this.httpStatusCode = httpStatusCode;
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
