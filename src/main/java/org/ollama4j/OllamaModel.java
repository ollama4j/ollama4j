package org.ollama4j;

public enum OllamaModel {
    LLAMA2("llama2"), MISTRAL("mistral"), MEDLLAMA2("medllama2");

    private final String model;

    OllamaModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }
}
