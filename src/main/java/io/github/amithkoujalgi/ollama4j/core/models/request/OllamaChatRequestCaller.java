package io.github.amithkoujalgi.ollama4j.core.models.request;

import io.github.amithkoujalgi.ollama4j.core.models.BasicAuth;

public class OllamaChatRequestCaller extends OllamaServerCaller{

    public OllamaChatRequestCaller(String host, BasicAuth basicAuth, long requestTimeoutSeconds, boolean verbose) {
        super(host, basicAuth, requestTimeoutSeconds, verbose);
    }

    @Override
    protected String getEndpointSuffix() {
        return "/api/generate";
    }

}
