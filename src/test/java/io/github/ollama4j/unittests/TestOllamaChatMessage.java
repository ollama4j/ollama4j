package io.github.ollama4j.unittests;

import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestOllamaChatMessage {

    @Test
    void testToStringProducesJson() {
        OllamaChatMessage msg = new OllamaChatMessage(OllamaChatMessageRole.USER, "hello", null, null, null);
        String json = msg.toString();
        JSONObject obj = new JSONObject(json);
        assertEquals("user", obj.getString("role"));
        assertEquals("hello", obj.getString("content"));
        assertTrue(obj.has("tool_calls"));
        // thinking and images may or may not be present depending on null handling, just ensure no exception
    }
}
