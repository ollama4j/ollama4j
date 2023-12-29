package io.github.amithkoujalgi.ollama4j.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
  public static ObjectMapper getObjectMapper() {
    return new ObjectMapper();
  }
}
