package io.github.amithkoujalgi.ollama4j.core.models;

import java.util.List;
import lombok.Data;

@Data
public class ListModelsResponse {
    private List<Model> models;
}
