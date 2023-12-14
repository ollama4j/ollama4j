package io.github.amithkoujalgi.ollama4j.core.models;


import lombok.Data;

@Data
public class OllamaRequestModel {
    private String model;
    private String prompt;

    public OllamaRequestModel(String model, String prompt) {
        this.model = model;
        this.prompt = prompt;
    }
}
