package io.github.ollama4j.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.Data;
import lombok.Getter;

import static io.github.ollama4j.utils.Utils.getObjectMapper;

import java.util.HashMap;
import java.util.Map;

/** The type Ollama result. */
@Getter
@SuppressWarnings("unused")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OllamaResult {
  /**
   * -- GETTER --
   * Get the completion/response text
   *
   * @return String completion/response text
   */
  private final String response;

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

  public OllamaResult(String response, long responseTime, int httpStatusCode) {
    this.response = response;
    this.responseTime = responseTime;
    this.httpStatusCode = httpStatusCode;
  }

  @Override
  public String toString() {
    try {
      Map<String, Object> responseMap = new HashMap<>();
      responseMap.put("response", this.response);
      responseMap.put("httpStatusCode", this.httpStatusCode);
      responseMap.put("responseTime", this.responseTime);
      return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(responseMap);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get the structured response if the response is a JSON object.
   *
   * @return Map - structured response
   * @throws IllegalArgumentException if the response is not a valid JSON object
   */
  public Map<String, Object> getStructuredResponse() {
    String responseStr = this.getResponse();
    if (responseStr == null || responseStr.trim().isEmpty()) {
      throw new IllegalArgumentException("Response is empty or null");
    }

    try {
      // Check if the response is a valid JSON
      if ((!responseStr.trim().startsWith("{") && !responseStr.trim().startsWith("[")) ||
          (!responseStr.trim().endsWith("}") && !responseStr.trim().endsWith("]"))) {
        throw new IllegalArgumentException("Response is not a valid JSON object");
      }

      Map<String, Object> response = getObjectMapper().readValue(responseStr,
          new TypeReference<Map<String, Object>>() {
          });
      return response;
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Failed to parse response as JSON: " + e.getMessage(), e);
    }
  }

  /**
   * Get the structured response mapped to a specific class type.
   *
   * @param <T> The type of class to map the response to
   * @param clazz The class to map the response to
   * @return An instance of the specified class with the response data
   * @throws IllegalArgumentException if the response is not a valid JSON or is empty
   * @throws RuntimeException if there is an error mapping the response
   */
  public <T> T as(Class<T> clazz) {
    String responseStr = this.getResponse();
    if (responseStr == null || responseStr.trim().isEmpty()) {
      throw new IllegalArgumentException("Response is empty or null");
    }

    try {
      // Check if the response is a valid JSON
      if ((!responseStr.trim().startsWith("{") && !responseStr.trim().startsWith("[")) ||
          (!responseStr.trim().endsWith("}") && !responseStr.trim().endsWith("]"))) {
        throw new IllegalArgumentException("Response is not a valid JSON object");
      }
      return getObjectMapper().readValue(responseStr, clazz);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Failed to parse response as JSON: " + e.getMessage(), e);
    }
  }
}
