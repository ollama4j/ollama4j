package org.ollama4j;

import com.google.gson.Gson;

public class OllamaRequestModel {
    private String model;
    private String prompt;

    public OllamaRequestModel(String model, String prompt) {
        this.model = model;
        this.prompt = prompt;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
