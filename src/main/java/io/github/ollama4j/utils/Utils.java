package io.github.ollama4j.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Utils {

  private static ObjectMapper objectMapper;

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
}
