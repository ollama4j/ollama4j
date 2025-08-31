package io.github.ollama4j.models.chat;

import io.github.ollama4j.models.generate.OllamaStreamHandler;
import io.github.ollama4j.models.generate.OllamaTokenHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OllamaChatStreamObserver implements OllamaTokenHandler {
    private final OllamaStreamHandler thinkingStreamHandler;
    private final OllamaStreamHandler responseStreamHandler;

    private String message = "";

    @Override
    public void accept(OllamaChatResponseModel token) {
        if (responseStreamHandler == null || token == null || token.getMessage() == null) {
            return;
        }

        String thinking = token.getMessage().getThinking();
        String content = token.getMessage().getContent();

        boolean hasThinking = thinking != null && !thinking.isEmpty();
        boolean hasContent = !content.isEmpty();

//        if (hasThinking && !hasContent) {
////            message += thinking;
//            message = thinking;
//        } else {
////            message += content;
//            message = content;
//        }
//
//        responseStreamHandler.accept(message);


        if (!hasContent && hasThinking && thinkingStreamHandler != null) {
            // message = message + thinking;

            // use only new tokens received, instead of appending the tokens to the previous
            // ones and sending the full string again
            thinkingStreamHandler.accept(thinking);
        } else if (hasContent && responseStreamHandler != null) {
            // message = message + response;

            // use only new tokens received, instead of appending the tokens to the previous
            // ones and sending the full string again
            responseStreamHandler.accept(content);
        }
    }
}
