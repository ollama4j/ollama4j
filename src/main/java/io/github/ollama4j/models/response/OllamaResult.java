package io.github.ollama4j.models.response;

import static io.github.ollama4j.utils.Utils.getObjectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.generate.OllamaGenerateRequest;
import lombok.Data;
import lombok.Getter;
import lombok.val;

import java.io.IOException;

/** The type Ollama result. */
@Getter
@SuppressWarnings("unused")
@Data
public class OllamaResult {
  /**
   * -- GETTER --
   *  Get the completion/response text
   *
   * @return String completion/response text
   */
  protected final String response;

  /**
   * -- GETTER --
   *  Get the response status code.
   *
   * @return int - response status code
   */
  protected int httpStatusCode;

  /**
   * -- GETTER --
   *  Get the response time in milliseconds.
   *
   * @return long - response time in milliseconds
   */
  protected long responseTime = 0;

  public OllamaResult(String response, long responseTime, int httpStatusCode) {
    this.response = response;
    this.responseTime = responseTime;
    this.httpStatusCode = httpStatusCode;
  }

  /**
   * Unmarshals and returns the response as the class specified in the {@link OllamaChatRequest} or {@link OllamaGenerateRequest}.
   * <p>
   * Hint: the OllamaChatRequestModel#getStream() property is not implemented.
   *
   * @return T
   * @throws IllegalStateException  if an attempt is made to get structured response when format was not supplied during initial request
   * @throws RuntimeException       if the parameterized type supplied for unmarshalling is incompatible with the type supplied on initial request
   */
  public <T> T getStructuredResponse(Class<T> responseType) {
    if (this.response == null || responseType == null) {
      throw new IllegalStateException("Response or responseType is null; cannot structure a response.");
    }

    try {
      @SuppressWarnings("unchecked")
      T deserializedValue = getObjectMapper().readValue(response, responseType);
      return deserializedValue;
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to parse response into type: " + responseType.getName(), e);
    }
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
