package io.github.ollama4j.models.response;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class LibraryModel {

    private String name;
    private String pullCount;
    private int numTags;
    private List<String> popularTags = new ArrayList<>();
    private String updatedAt;
}
