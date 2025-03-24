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

/** The type Ollama result. */
@Getter
@SuppressWarnings("unused")
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OllamaResult {
  /**
   * -- GETTER --
   * Get the completion/response text
   *
   * @return String completion/response text
   */
  private String response;

  /**
   * -- GETTER --
   * Get the response status code.
   *
   * @return int - response status code
   */
  private int httpStatusCode;

  /**
   * -- GETTER --
   * Get the response time in milliseconds.
   *
   * @return long - response time in milliseconds
   */
  private long responseTime = 0;

  /**
   * -- GETTER --
   * Get the model name used for the response.
   *
   * @return String - model name
   */
  private String model;

  @JsonCreator
  public OllamaResult(@JsonProperty("response") String response) {
    this.response = response;
  }

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
