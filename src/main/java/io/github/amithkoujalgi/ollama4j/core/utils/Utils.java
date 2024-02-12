package io.github.amithkoujalgi.ollama4j.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
  public static ObjectMapper getObjectMapper() {
    return new ObjectMapper();
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
