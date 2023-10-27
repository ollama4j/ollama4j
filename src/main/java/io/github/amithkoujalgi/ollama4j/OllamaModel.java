package io.github.amithkoujalgi.ollama4j;

public enum OllamaModel {
    LLAMA2("llama2"), MISTRAL("mistral"), MEDLLAMA2("medllama2"), CODELLAMA("codellama"), VICUNA("vicuna"), ORCAMINI("orca-mini"), sqlcoder("sqlcoder"), WIZARDMATH("wizard-math");

    private final String model;

    OllamaModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }
}
