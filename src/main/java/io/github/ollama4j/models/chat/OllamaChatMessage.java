package io.github.ollama4j.models.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.ollama4j.utils.FileToBase64Serializer;
import lombok.*;

import java.util.List;

import static io.github.ollama4j.utils.Utils.getObjectMapper;

/**
 * Defines a single Message to be used inside a chat request against the ollama /api/chat endpoint.
 *
 * @see <a href="https://github.com/ollama/ollama/blob/main/docs/api.md#generate-a-chat-completion">Generate chat completion</a>
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OllamaChatMessage {

    @NonNull
    private OllamaChatMessageRole role;

    @NonNull
    private String content;

    private String thinking;

    private @JsonProperty("tool_calls") List<OllamaChatToolCalls> toolCalls;

    @JsonSerialize(using = FileToBase64Serializer.class)
    private List<byte[]> images;

    @Override
    public String toString() {
        try {
            return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
