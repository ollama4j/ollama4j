package io.github.amithkoujalgi.ollama4j.core.models;

import java.util.List;

public class ListModelsResponse {
    private List<Model> models;

    public List<Model> getModels() {
        return models;
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }
}
