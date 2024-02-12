package io.github.amithkoujalgi.ollama4j.core.models.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import static io.github.amithkoujalgi.ollama4j.core.utils.Utils.getObjectMapper;

import java.io.File;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Defines a single Message to be used inside a chat request against the ollama /api/chat endpoint.
 * 
 * @see https://github.com/ollama/ollama/blob/main/docs/api.md#generate-a-chat-completion
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class OllamaChatMessage {

    @NonNull
    private OllamaChatMessageRole role;

    @NonNull
    private String content;

    private List<File> images;
    
      @Override
  public String toString() {
    try {
      return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
