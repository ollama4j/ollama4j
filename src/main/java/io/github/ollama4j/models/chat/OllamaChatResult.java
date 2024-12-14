package io.github.ollama4j.models.chat;

import java.util.List;

import io.github.ollama4j.models.response.OllamaResult;

/**
 * Specific chat-API result that contains the chat history sent to the model and appends the answer as {@link OllamaChatResult} given by the
 * {@link OllamaChatMessageRole#ASSISTANT} role.
 */
public class OllamaChatResult extends OllamaResult {

    private List<OllamaChatMessage> chatHistory;

    public OllamaChatResult(String response, long responseTime, int httpStatusCode) {
        super(response, responseTime, httpStatusCode);
    }

    public OllamaChatResult(String response, long responseTime, int httpStatusCode, List<OllamaChatMessage> chatHistory) {
        super(response, responseTime, httpStatusCode);
        this.chatHistory = chatHistory;
        appendAnswerToChatHistory(response);
    }

    public List<OllamaChatMessage> getChatHistory() {
        return chatHistory;
    }

    private void appendAnswerToChatHistory(String answer) {
        OllamaChatMessage assistantMessage = new OllamaChatMessage(OllamaChatMessageRole.ASSISTANT, answer);
        this.chatHistory.add(assistantMessage);
    }
}
