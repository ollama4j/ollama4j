package io.github.ollama4j.utils;

import lombok.Data;

import java.util.Map;

/**
 * Class for options for Ollama model.
 */
@Data
public class Options {

    private final Map<String, Object> optionsMap;
}
