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
        if (streamHandler == null || token == null || token.getMessage() == null) {
            return;
        }

        String content = token.getMessage().getContent();
        String thinking = token.getMessage().getThinking();

        boolean hasContent = !content.isEmpty();
        boolean hasThinking = thinking != null && !thinking.isEmpty();

        if (hasThinking && !hasContent) {
            message += thinking;
        } else {
            message += content;
        }

        streamHandler.accept(message);
    }
}
