package io.github.ollama4j.models.embeddings;

import io.github.ollama4j.utils.Options;

import java.util.List;

/**
 * Builder class to easily create Requests for Embedding models using ollama.
 */
public class OllamaEmbedRequestBuilder {

    private final OllamaEmbedRequestModel request;

    private OllamaEmbedRequestBuilder(String model, List<String> input) {
        this.request = new OllamaEmbedRequestModel(model,input);
    }

    public static OllamaEmbedRequestBuilder getInstance(String model, String... input){
        return new OllamaEmbedRequestBuilder(model, List.of(input));
    }

    public OllamaEmbedRequestBuilder withOptions(Options options){
        this.request.setOptions(options.getOptionsMap());
        return this;
    }

    public OllamaEmbedRequestBuilder withKeepAlive(String keepAlive){
        this.request.setKeepAlive(keepAlive);
        return this;
    }

    public OllamaEmbedRequestBuilder withoutTruncate(){
        this.request.setTruncate(false);
        return this;
    }

    public OllamaEmbedRequestModel build() {
        return this.request;
    }
}
