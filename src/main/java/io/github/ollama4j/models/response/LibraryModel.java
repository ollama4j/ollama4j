package io.github.ollama4j.models.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LibraryModel {

    private String name;
    private String description;
    private String pullCount;
    private int totalTags;
    private List<String> popularTags = new ArrayList<>();
    private String lastUpdated;
}
