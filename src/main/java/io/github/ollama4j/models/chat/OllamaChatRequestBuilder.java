/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.models.chat;

import io.github.ollama4j.utils.Options;
import io.github.ollama4j.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Helper class for creating {@link OllamaChatRequest} objects using the builder-pattern. */
public class OllamaChatRequestBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(OllamaChatRequestBuilder.class);

    private int imageURLConnectTimeoutSeconds = 10;
    private int imageURLReadTimeoutSeconds = 10;

    @Setter private boolean useTools = true;

    public OllamaChatRequestBuilder withImageURLConnectTimeoutSeconds(
            int imageURLConnectTimeoutSeconds) {
        this.imageURLConnectTimeoutSeconds = imageURLConnectTimeoutSeconds;
        return this;
    }

    public OllamaChatRequestBuilder withImageURLReadTimeoutSeconds(int imageURLReadTimeoutSeconds) {
        this.imageURLReadTimeoutSeconds = imageURLReadTimeoutSeconds;
        return this;
    }

    private OllamaChatRequestBuilder(String model, List<OllamaChatMessage> messages) {
        request = new OllamaChatRequest(model, false, messages);
    }

    private OllamaChatRequest request;

    public static OllamaChatRequestBuilder getInstance(String model) {
        return new OllamaChatRequestBuilder(model, new ArrayList<>());
    }

    public OllamaChatRequest build() {
        request.setUseTools(useTools);
        return request;
    }

    public void reset() {
        request = new OllamaChatRequest(request.getModel(), request.isThink(), new ArrayList<>());
    }

    public OllamaChatRequestBuilder withMessage(OllamaChatMessageRole role, String content) {
        return withMessage(role, content, Collections.emptyList());
    }

    public OllamaChatRequestBuilder withMessage(
            OllamaChatMessageRole role, String content, List<OllamaChatToolCalls> toolCalls) {
        List<OllamaChatMessage> messages = this.request.getMessages();
        messages.add(new OllamaChatMessage(role, content, null, toolCalls, null));
        return this;
    }

    public OllamaChatRequestBuilder withMessage(
            OllamaChatMessageRole role,
            String content,
            List<OllamaChatToolCalls> toolCalls,
            List<File> images) {
        List<OllamaChatMessage> messages = this.request.getMessages();

        List<byte[]> binaryImages =
                images.stream()
                        .map(
                                file -> {
                                    try {
                                        return Files.readAllBytes(file.toPath());
                                    } catch (IOException e) {
                                        LOG.warn(
                                                "File '{}' could not be accessed, will not add to"
                                                        + " message!",
                                                file.toPath(),
                                                e);
                                        return new byte[0];
                                    }
                                })
                        .collect(Collectors.toList());

        messages.add(new OllamaChatMessage(role, content, null, toolCalls, binaryImages));
        return this;
    }

    public OllamaChatRequestBuilder withMessage(
            OllamaChatMessageRole role,
            String content,
            List<OllamaChatToolCalls> toolCalls,
            String... imageUrls)
            throws IOException, InterruptedException {
        List<OllamaChatMessage> messages = this.request.getMessages();
        List<byte[]> binaryImages = null;
        if (imageUrls.length > 0) {
            binaryImages = new ArrayList<>();
            for (String imageUrl : imageUrls) {
                try {
                    binaryImages.add(
                            Utils.loadImageBytesFromUrl(
                                    imageUrl,
                                    imageURLConnectTimeoutSeconds,
                                    imageURLReadTimeoutSeconds));
                } catch (InterruptedException e) {
                    LOG.error("Failed to load image from URL: '{}'. Cause: {}", imageUrl, e);
                    Thread.currentThread().interrupt();
                    throw new InterruptedException(
                            "Interrupted while loading image from URL: " + imageUrl);
                } catch (IOException e) {
                    LOG.error(
                            "IOException occurred while loading image from URL '{}'. Cause: {}",
                            imageUrl,
                            e.getMessage(),
                            e);
                    throw new IOException(
                            "IOException while loading image from URL: " + imageUrl, e);
                }
            }
        }

        messages.add(new OllamaChatMessage(role, content, null, toolCalls, binaryImages));
        return this;
    }

    public OllamaChatRequestBuilder withMessages(List<OllamaChatMessage> messages) {
        return new OllamaChatRequestBuilder(request.getModel(), messages);
    }

    public OllamaChatRequestBuilder withOptions(Options options) {
        this.request.setOptions(options.getOptionsMap());
        return this;
    }

    public OllamaChatRequestBuilder withGetJsonResponse() {
        this.request.setFormat("json");
        return this;
    }

    public OllamaChatRequestBuilder withTemplate(String template) {
        this.request.setTemplate(template);
        return this;
    }

    public OllamaChatRequestBuilder withStreaming() {
        this.request.setStream(true);
        return this;
    }

    public OllamaChatRequestBuilder withKeepAlive(String keepAlive) {
        this.request.setKeepAlive(keepAlive);
        return this;
    }

    public OllamaChatRequestBuilder withThinking(boolean think) {
        this.request.setThink(think);
        return this;
    }
}
