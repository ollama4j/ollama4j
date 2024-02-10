package io.github.amithkoujalgi.ollama4j.core.models.chat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.amithkoujalgi.ollama4j.core.utils.Options;

/**
 * Helper class for creating {@link OllamaChatRequestModel} objects using the builder-pattern.
 */
public class OllamaChatRequestBuilder {

    private OllamaChatRequestBuilder(String model, List<OllamaChatMessage> messages){
        request = new OllamaChatRequestModel(model, messages);
    }

    private OllamaChatRequestModel request;

    public static OllamaChatRequestBuilder getInstance(String model){
        return new OllamaChatRequestBuilder(model, new ArrayList<>());
    }

    public OllamaChatRequestModel build(){
        return request;
    }

    public OllamaChatRequestBuilder withMessage(OllamaChatMessageRole role, String content, File... images){
        List<OllamaChatMessage> messages = this.request.getMessages();
        messages.add(new OllamaChatMessage(role,content,List.of(images)));
        return this;
    }

    public OllamaChatRequestBuilder withMessages(List<OllamaChatMessage> messages){
        this.request.getMessages().addAll(messages);
        return this;
    }

    public OllamaChatRequestBuilder withOptions(Options options){
        this.request.setOptions(options);
        return this;
    }

    public OllamaChatRequestBuilder withFormat(String format){
        this.request.setFormat(format);
        return this;
    }

    public OllamaChatRequestBuilder withTemplate(String template){
        this.request.setTemplate(template);
        return this;
    }

    public OllamaChatRequestBuilder withStreaming(){
        this.request.setStream(true);
        return this;
    }

    public OllamaChatRequestBuilder withKeepAlive(String keepAlive){
        this.request.setKeepAlive(keepAlive);
        return this;
    }

}
