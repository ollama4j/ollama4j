package io.github.ollama4j.models.chat;

import io.github.ollama4j.models.generate.OllamaStreamHandler;
import io.github.ollama4j.models.generate.OllamaTokenHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OllamaChatStreamObserver implements OllamaTokenHandler {
    private final OllamaStreamHandler streamHandler;
    private String message = "";

    @Override
    public void accept(OllamaChatResponseModel token) {
        if (streamHandler != null) {
            message += token.getMessage().getContent();
            streamHandler.accept(message);
        }
    }
}
