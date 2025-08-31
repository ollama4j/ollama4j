package io.github.ollama4j.models.response;

import lombok.Data;

import java.util.List;

@Data
public class ListModelsResponse {
    private List<Model> models;
}
