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
        message = message + currentResponsePart.getResponse();
        streamHandler.accept(message);
    }


}
