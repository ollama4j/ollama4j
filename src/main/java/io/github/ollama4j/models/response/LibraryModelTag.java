package io.github.ollama4j.models.response;

import lombok.Data;

@Data
public class LibraryModelTag {
    private String name;
    private String tag;
    private String size;
    private String lastUpdated;
}
