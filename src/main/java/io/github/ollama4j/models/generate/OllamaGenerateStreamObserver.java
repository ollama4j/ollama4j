package io.github.ollama4j.models.generate;

import java.util.ArrayList;
import java.util.List;

public class OllamaGenerateStreamObserver {

    private OllamaStreamHandler streamHandler;

    private List<OllamaGenerateResponseModel> responseParts = new ArrayList<>();

    private String message = "";

    public OllamaGenerateStreamObserver(OllamaStreamHandler streamHandler) {
        this.streamHandler = streamHandler;
    }

    public void notify(OllamaGenerateResponseModel currentResponsePart) {
        responseParts.add(currentResponsePart);
        handleCurrentResponsePart(currentResponsePart);
    }

    protected void handleCurrentResponsePart(OllamaGenerateResponseModel currentResponsePart) {
        String response = currentResponsePart.getResponse();
        String thinking = currentResponsePart.getThinking();

        boolean hasResponse = response != null && !response.trim().isEmpty();
        boolean hasThinking = thinking != null && !thinking.trim().isEmpty();

        if (!hasResponse && hasThinking) {
            message = message + thinking;
        } else if (hasResponse) {
            message = message + response;
        }
        streamHandler.accept(message);
    }
}
