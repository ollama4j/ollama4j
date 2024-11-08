package io.github.ollama4j.models.response;

import lombok.Data;

import java.util.List;

@Data
public class LibraryModelDetail {

    private LibraryModel model;
    private List<LibraryModelTag> tags;
}
