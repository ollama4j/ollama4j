package io.github.ollama4j.models.generate;

import io.github.ollama4j.models.chat.OllamaChatResponseModel;

import java.util.function.Consumer;

public interface OllamaTokenHandler extends Consumer<OllamaChatResponseModel> {
}
