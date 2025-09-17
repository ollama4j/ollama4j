/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.ollama4j.utils.OllamaRequestBody;
import io.github.ollama4j.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Flow;
import org.junit.jupiter.api.Test;

class TestOllamaRequestBody {

    static class SimpleRequest implements OllamaRequestBody {
        public String name;
        public int value;

        SimpleRequest(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    @Test
    void testGetBodyPublisherProducesSerializedJson() throws IOException {
        SimpleRequest req = new SimpleRequest("abc", 123);

        var publisher = req.getBodyPublisher();

        StringBuilder data = new StringBuilder();
        publisher.subscribe(
                new Flow.Subscriber<>() {
                    @Override
                    public void onSubscribe(Flow.Subscription subscription) {
                        subscription.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(ByteBuffer item) {
                        data.append(StandardCharsets.UTF_8.decode(item));
                    }

                    @Override
                    // This method is intentionally left empty because, for this test,
                    // we do not expect any errors to occur during synchronous publishing.
                    // If an error does occur, the test will fail elsewhere.
                    public void onError(Throwable throwable) {
                        // No action needed for this test
                    }

                    @Override
                    public void onComplete() {
                        // This method is intentionally left empty because, for this test,
                        // we do not need to perform any action when the publishing completes.
                        // The assertion is performed after subscription, and no cleanup or
                        // further processing is required here.
                    }
                });

        // Trigger the publishing by converting it to a string via the same mapper for determinism
        String expected = Utils.getObjectMapper().writeValueAsString(req);
        // Due to asynchronous nature, expected content already delivered synchronously by
        // StringPublisher
        assertEquals(expected, data.toString());
    }
}
