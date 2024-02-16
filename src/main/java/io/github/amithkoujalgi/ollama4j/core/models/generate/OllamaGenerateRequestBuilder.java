package io.github.amithkoujalgi.ollama4j.core.models.generate;

import io.github.amithkoujalgi.ollama4j.core.utils.Options;

/**
 * Helper class for creating {@link io.github.amithkoujalgi.ollama4j.core.models.generate.OllamaGenerateRequestModel} 
 * objects using the builder-pattern.
 */
public class OllamaGenerateRequestBuilder {

    private OllamaGenerateRequestBuilder(String model, String prompt){
        request = new OllamaGenerateRequestModel(model, prompt);
    }

    private OllamaGenerateRequestModel request;

    public static OllamaGenerateRequestBuilder getInstance(String model){
        return new OllamaGenerateRequestBuilder(model,"");
    }

    public OllamaGenerateRequestModel build(){
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
