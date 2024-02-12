package io.github.amithkoujalgi.ollama4j.core.models.chat;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.github.amithkoujalgi.ollama4j.core.utils.OllamaRequestBody;
import io.github.amithkoujalgi.ollama4j.core.utils.Options;

import static io.github.amithkoujalgi.ollama4j.core.utils.Utils.getObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Defines a Request to use against the ollama /api/chat endpoint.
 * 
 * @see https://github.com/ollama/ollama/blob/main/docs/api.md#generate-a-chat-completion
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class OllamaChatRequestModel implements OllamaRequestBody{

    @NonNull
    private String model;

    @NonNull
    private List<OllamaChatMessage> messages;

    private String format;
    private Options options;
    private String template;
    private boolean stream;
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