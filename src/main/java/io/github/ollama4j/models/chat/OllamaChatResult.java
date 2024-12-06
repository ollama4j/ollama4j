package io.github.ollama4j.models.chat;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ollama4j.models.response.OllamaResult;
import lombok.Getter;

import static io.github.ollama4j.utils.Utils.getObjectMapper;

/**
 * Specific chat-API result that contains the chat history sent to the model and appends the answer as {@link OllamaChatResult} given by the
 * {@link OllamaChatMessageRole#ASSISTANT} role.
 */
@Getter
public class OllamaChatResult {


    private List<OllamaChatMessage> chatHistory;

    private OllamaChatResponseModel response;

    public OllamaChatResult(OllamaChatResponseModel response, List<OllamaChatMessage> chatHistory) {
        this.chatHistory = chatHistory;
        this.response = response;
        appendAnswerToChatHistory(response);
    }

    private void appendAnswerToChatHistory(OllamaChatResponseModel response) {
        this.chatHistory.add(response.getMessage());
    }

    @Override
    public String toString() {
        try {
            return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
