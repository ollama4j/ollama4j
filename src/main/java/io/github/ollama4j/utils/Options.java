package io.github.ollama4j.utils;

import java.util.Map;
import lombok.Data;

/** Class for options for Ollama model. */
@Data
public class Options {

  private final Map<String, Object> optionsMap;
}
