package io.github.ollama4j.unittests;

import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TestOllamaChatRequestBuilder {

    @Test
    void testResetClearsMessagesButKeepsModelAndThink() {
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance("my-model")
                .withThinking(true)
                .withMessage(OllamaChatMessageRole.USER, "first");

        OllamaChatRequest beforeReset = builder.build();
        assertEquals("my-model", beforeReset.getModel());
        assertTrue(beforeReset.isThink());
        assertEquals(1, beforeReset.getMessages().size());

        builder.reset();
        OllamaChatRequest afterReset = builder.build();
        assertEquals("my-model", afterReset.getModel());
        assertTrue(afterReset.isThink());
        assertNotNull(afterReset.getMessages());
        assertEquals(0, afterReset.getMessages().size());
    }

    @Test
    void testImageUrlFailuresAreIgnoredAndDoNotBreakBuild() {
        // Provide clearly invalid URL, builder logs a warning and continues
        OllamaChatRequest req = OllamaChatRequestBuilder.getInstance("m")
                .withMessage(OllamaChatMessageRole.USER, "hi", Collections.emptyList(),
                        "ht!tp://invalid url \n not a uri")
                .build();

        assertNotNull(req.getMessages());
        assertEquals(1, req.getMessages().size());
        OllamaChatMessage msg = req.getMessages().get(0);
        // images list will be initialized only if any valid URL was added; for invalid URL list can be null
        // We just assert that builder didn't crash and message is present with content
        assertEquals("hi", msg.getContent());
    }
}
