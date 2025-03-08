package io.github.ollama4j.models.chat;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;

import static io.github.ollama4j.utils.Utils.getObjectMapper;

/**
 * Specific chat-API result that contains the chat history sent to the model and appends the answer as {@link OllamaChatResult} given by the
 * {@link OllamaChatMessageRole#ASSISTANT} role.
 */
@Getter
public class OllamaChatResult {

    private final List<OllamaChatMessage> chatHistory;

    private final OllamaChatResponseModel responseModel;

    public OllamaChatResult(OllamaChatResponseModel responseModel, List<OllamaChatMessage> chatHistory) {
        this.chatHistory = chatHistory;
        this.responseModel = responseModel;
        appendAnswerToChatHistory(responseModel);
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

    @Deprecated
    public String getResponse(){
        return responseModel != null ? responseModel.getMessage().getContent() : "";
    }

    @Deprecated
    public int getHttpStatusCode(){
        return 200;
    }

    @Deprecated
    public long getResponseTime(){
        return responseModel != null ? responseModel.getTotalDuration() : 0L;
    }
}
