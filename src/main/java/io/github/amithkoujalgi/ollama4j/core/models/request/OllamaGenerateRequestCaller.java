package io.github.amithkoujalgi.ollama4j.core.models.request;

import io.github.amithkoujalgi.ollama4j.core.models.BasicAuth;

public class OllamaGenerateRequestCaller extends OllamaServerCaller{

    public OllamaGenerateRequestCaller(String host, BasicAuth basicAuth, long requestTimeoutSeconds, boolean verbose) {
        super(host, basicAuth, requestTimeoutSeconds, verbose);   
    }

    @Override
    protected String getEndpointSuffix() {
        return "/api/generate";
    }

    
    
}
