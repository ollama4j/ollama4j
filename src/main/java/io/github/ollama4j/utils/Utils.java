package io.github.ollama4j.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.victools.jsonschema.generator.*;

public class Utils {

  private static ObjectMapper objectMapper;
  private static final SchemaGeneratorConfigBuilder configBuilder =
          new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON);
  private static SchemaGenerator generator = null;
  private static final Map<Class<?>, JsonNode> schemaCache = new ConcurrentHashMap<Class<?>, JsonNode>();

  public static ObjectMapper getObjectMapper() {
    if(objectMapper == null) {
      objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
    }
    return objectMapper;
  }

  public static byte[] loadImageBytesFromUrl(String imageUrl)
      throws IOException, URISyntaxException {
    URL url = new URI(imageUrl).toURL();
    try (InputStream in = url.openStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = in.read(buffer)) != -1) {
        out.write(buffer, 0, bytesRead);
      }
      return out.toByteArray();
    }
  }

  public static JsonNode generateJsonSchema(Class<?> format) {
    if(generator == null) {
      configBuilder.forFields().withRequiredCheck(field -> true);
      generator = new SchemaGenerator(configBuilder.build());
    }
    return schemaCache.computeIfAbsent(format, k -> generator.generateSchema(format));
  }
}
