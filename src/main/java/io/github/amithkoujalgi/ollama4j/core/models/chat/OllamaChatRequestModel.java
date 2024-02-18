package io.github.amithkoujalgi.ollama4j.core.models.chat;

import java.util.List;
import io.github.amithkoujalgi.ollama4j.core.models.OllamaCommonRequestModel;
import io.github.amithkoujalgi.ollama4j.core.utils.OllamaRequestBody;

import lombok.Getter;
import lombok.Setter;

/**
 * Defines a Request to use against the ollama /api/chat endpoint.
 *
 * @see <a href=
 *      "https://github.com/ollama/ollama/blob/main/docs/api.md#generate-a-chat-completion">Generate
 *      Chat Completion</a>
 */
@Getter
@Setter
public class OllamaChatRequestModel extends OllamaCommonRequestModel implements OllamaRequestBody {

  private List<OllamaChatMessage> messages;

  public OllamaChatRequestModel() {}

  public OllamaChatRequestModel(String model, List<OllamaChatMessage> messages) {
    this.model = model;
    this.messages = messages;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof OllamaChatRequestModel)) {
      return false;
    }

    return this.toString().equals(o.toString());
  }

}
