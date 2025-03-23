package io.github.ollama4j.models.response;

import static io.github.ollama4j.utils.Utils.getObjectMapper;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@SuppressWarnings("unused")
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OllamaStructuredResult {
  private String response;

  private int httpStatusCode;

  private long responseTime = 0;

  private String model;

  public OllamaStructuredResult(String response, long responseTime, int httpStatusCode) {
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

  /**
   * Get the structured response if the response is a JSON object.
   *
   * @return Map - structured response
   */
  public Map<String, Object> getStructuredResponse() {
    try {
      Map<String, Object> response = getObjectMapper().readValue(this.getResponse(),
          new TypeReference<Map<String, Object>>() {
          });
      return response;
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get the structured response mapped to a specific class type.
   *
   * @param <T> The type of class to map the response to
   * @param clazz The class to map the response to
   * @return An instance of the specified class with the response data
   * @throws RuntimeException if there is an error mapping the response
   */
  public <T> T getStructuredResponse(Class<T> clazz) {
    try {
      return getObjectMapper().readValue(this.getResponse(), clazz);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
