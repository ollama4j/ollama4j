/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.ollama4j.models.embed.OllamaEmbedRequest;
import io.github.ollama4j.models.embed.OllamaEmbedRequestBuilder;
import io.github.ollama4j.utils.OptionsBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestEmbedRequestSerialization extends AbstractSerializationTest<OllamaEmbedRequest> {

    private OllamaEmbedRequestBuilder builder;

    @BeforeEach
    public void init() {
        builder = OllamaEmbedRequestBuilder.getInstance("DummyModel", "DummyPrompt");
    }

    @Test
    public void testRequestOnlyMandatoryFields() {
        OllamaEmbedRequest req = builder.build();
        String jsonRequest = serialize(req);
        assertEqualsAfterUnmarshalling(deserialize(jsonRequest, OllamaEmbedRequest.class), req);
    }

    @Test
    public void testRequestWithOptions() {
        OptionsBuilder b = new OptionsBuilder();
        OllamaEmbedRequest req = builder.withOptions(b.setMirostat(1).build()).build();

        String jsonRequest = serialize(req);
        OllamaEmbedRequest deserializeRequest = deserialize(jsonRequest, OllamaEmbedRequest.class);
        assertEqualsAfterUnmarshalling(deserializeRequest, req);
        assertEquals(1, deserializeRequest.getOptions().get("mirostat"));
    }
}
