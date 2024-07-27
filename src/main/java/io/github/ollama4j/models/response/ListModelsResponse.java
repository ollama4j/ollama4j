package io.github.ollama4j.models.response;

import java.util.List;

import lombok.Data;

@Data
public class ListModelsResponse {
    private List<Model> models;
}
