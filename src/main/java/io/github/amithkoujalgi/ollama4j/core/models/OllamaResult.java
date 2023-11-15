package io.github.amithkoujalgi.ollama4j.core.models;

@SuppressWarnings("unused")
public class OllamaResult {
    private String response;
    private long responseTime = 0;

    public OllamaResult(String response, long responseTime) {
        this.response = response;
        this.responseTime = responseTime;
    }

    public String getResponse() {
        return response;
    }

    public long getResponseTime() {
        return responseTime;
    }
}
