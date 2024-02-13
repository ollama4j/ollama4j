package io.github.amithkoujalgi.ollama4j.core.models.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.amithkoujalgi.ollama4j.core.OllamaStreamHandler;
import lombok.NonNull;

public class OllamaChatStreamObserver {

    private OllamaStreamHandler streamHandler;

    private List<OllamaChatResponseModel> responseParts = new ArrayList<>();

    private String message;

    public OllamaChatStreamObserver(OllamaStreamHandler streamHandler) {
        this.streamHandler = streamHandler;
    }

    public void notify(OllamaChatResponseModel currentResponsePart){
        responseParts.add(currentResponsePart);
        handleCurrentResponsePart(currentResponsePart);
    }
    
    protected void handleCurrentResponsePart(OllamaChatResponseModel currentResponsePart){
        List<@NonNull String> allResponsePartsByNow = responseParts.stream().map(r -> r.getMessage().getContent()).collect(Collectors.toList());
        message = String.join("", allResponsePartsByNow);
        streamHandler.accept(message);
    }


}
