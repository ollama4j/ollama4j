package io.github.ollama4j.models.chat;

import io.github.ollama4j.tools.OllamaToolCallsFunction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OllamaChatToolCalls {

    private OllamaToolCallsFunction function;


}
