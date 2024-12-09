package io.github.ollama4j.models.chat;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.ollama4j.models.response.OllamaResult;

import static io.github.ollama4j.utils.Utils.getObjectMapper;

/**
 * Specific chat-API result that contains the chat history sent to the model and appends the answer as {@link OllamaChatResult} given by the
 * {@link OllamaChatMessageRole#ASSISTANT} role.
 */
public class OllamaChatResult extends OllamaResult {

    private List<OllamaChatMessage> chatHistory;
    private Object structuredResponse;
    private Class<?> responseType;

    public OllamaChatResult(String response, long responseTime, int httpStatusCode) {
        super(response, responseTime, httpStatusCode);
    }

    public OllamaChatResult(String response, long responseTime, int httpStatusCode, List<OllamaChatMessage> chatHistory) {
        super(response, responseTime, httpStatusCode);
        this.chatHistory = chatHistory;
        appendAnswerToChatHistory(response);
    }

    @SuppressWarnings("unchecked") // TODO - better way to do all this?
    public <T> T getStructuredResponse() {
        return (T) this.structuredResponse;
    }

    public OllamaChatResult(String response, long responseTime, int httpStatusCode, List<OllamaChatMessage> chatHistory, Class<?> responseType) throws JsonProcessingException {
        super(response, responseTime, httpStatusCode);
        this.chatHistory = chatHistory;
        if(responseType != null) {
            JsonNode jsonNode = getObjectMapper().readTree(response);
            this.structuredResponse = getObjectMapper().convertValue(jsonNode, responseType);
        }
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
