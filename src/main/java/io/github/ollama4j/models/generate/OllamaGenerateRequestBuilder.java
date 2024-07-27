package io.github.ollama4j.models.generate;

import io.github.ollama4j.utils.Options;

/**
 * Helper class for creating {@link OllamaGenerateRequest}
 * objects using the builder-pattern.
 */
public class OllamaGenerateRequestBuilder {

    private OllamaGenerateRequestBuilder(String model, String prompt){
        request = new OllamaGenerateRequest(model, prompt);
    }

    private OllamaGenerateRequest request;

    public static OllamaGenerateRequestBuilder getInstance(String model){
        return new OllamaGenerateRequestBuilder(model,"");
    }

    public OllamaGenerateRequest build(){
        return request;
    }

    public OllamaGenerateRequestBuilder withPrompt(String prompt){
        request.setPrompt(prompt);
        return this;
    }
    
    public OllamaGenerateRequestBuilder withGetJsonResponse(){
        this.request.setReturnFormatJson(true);
        return this;
    }

    public OllamaGenerateRequestBuilder withOptions(Options options){
        this.request.setOptions(options.getOptionsMap());
        return this;
    }

    public OllamaGenerateRequestBuilder withTemplate(String template){
        this.request.setTemplate(template);
        return this;
    }

    public OllamaGenerateRequestBuilder withStreaming(){
        this.request.setStream(true);
        return this;
    }

    public OllamaGenerateRequestBuilder withKeepAlive(String keepAlive){
        this.request.setKeepAlive(keepAlive);
        return this;
    }

}
