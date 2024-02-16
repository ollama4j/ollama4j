package io.github.amithkoujalgi.ollama4j.core.models.chat;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.github.amithkoujalgi.ollama4j.core.models.OllamaCommonRequestModel;
import io.github.amithkoujalgi.ollama4j.core.utils.OllamaRequestBody;

import static io.github.amithkoujalgi.ollama4j.core.utils.Utils.getObjectMapper;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * Defines a Request to use against the ollama /api/chat endpoint.
 *
 * @see <a
 *     href="https://github.com/ollama/ollama/blob/main/docs/api.md#generate-a-chat-completion">Generate
 *     Chat Completion</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OllamaChatRequestModel extends OllamaCommonRequestModel implements OllamaRequestBody {

  @NonNull private List<OllamaChatMessage> messages;

  public OllamaChatRequestModel(String model,List<OllamaChatMessage> messages){
    super(model);
    this.messages = messages;
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
