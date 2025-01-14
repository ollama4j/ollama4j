package io.github.ollama4j.models.chat;

import io.github.ollama4j.utils.Options;
import io.github.ollama4j.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class for creating {@link OllamaChatRequest} objects using the builder-pattern.
 */
public class OllamaChatRequestBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(OllamaChatRequestBuilder.class);

    private OllamaChatRequestBuilder(String model, List<OllamaChatMessage> messages) {
        request = new OllamaChatRequest(model, messages);
    }

    private OllamaChatRequest request;

    public static OllamaChatRequestBuilder getInstance(String model) {
        return new OllamaChatRequestBuilder(model, new ArrayList<>());
    }

    public OllamaChatRequest build() {
        return request;
    }

    public void reset() {
        request = new OllamaChatRequest(request.getModel(), new ArrayList<>());
    }

    public OllamaChatRequestBuilder withMessage(OllamaChatMessageRole role, String content){
        return withMessage(role,content, Collections.emptyList());
    }

    public OllamaChatRequestBuilder withMessage(OllamaChatMessageRole role, String content, List<OllamaChatToolCalls> toolCalls,List<File> images) {
        List<OllamaChatMessage> messages = this.request.getMessages();

        List<byte[]> binaryImages = images.stream().map(file -> {
            try {
                return Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                LOG.warn("File '{}' could not be accessed, will not add to message!", file.toPath(), e);
                return new byte[0];
            }
        }).collect(Collectors.toList());

        messages.add(new OllamaChatMessage(role, content,toolCalls, binaryImages));
        return this;
    }

    public OllamaChatRequestBuilder withMessage(OllamaChatMessageRole role, String content,List<OllamaChatToolCalls> toolCalls, String... imageUrls) {
        List<OllamaChatMessage> messages = this.request.getMessages();
        List<byte[]> binaryImages = null;
        if (imageUrls.length > 0) {
            binaryImages = new ArrayList<>();
            for (String imageUrl : imageUrls) {
                try {
                    binaryImages.add(Utils.loadImageBytesFromUrl(imageUrl));
                } catch (URISyntaxException e) {
                    LOG.warn("URL '{}' could not be accessed, will not add to message!", imageUrl, e);
                } catch (IOException e) {
                    LOG.warn("Content of URL '{}' could not be read, will not add to message!", imageUrl, e);
                }
            }
        }

        messages.add(new OllamaChatMessage(role, content,toolCalls, binaryImages));
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
        this.request.setReturnFormatJson(true);
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

}
