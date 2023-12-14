package io.github.amithkoujalgi.ollama4j.integrationtests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestRealAPIs {

  OllamaAPI ollamaAPI;

  @BeforeEach
  void setUp() {
    String ollamaHost = "http://localhost:11434";
    ollamaAPI = new OllamaAPI(ollamaHost);
  }

  @Test
  void testWrongEndpoint() {
    OllamaAPI ollamaAPI = new OllamaAPI("http://wrong-host:11434");
    assertThrows(ConnectException.class, ollamaAPI::listModels);
  }

  @Test
  void testListModels() {
    try {
      assertNotNull(ollamaAPI.listModels());
      ollamaAPI.listModels().forEach(System.out::println);
    } catch (IOException | OllamaBaseException | InterruptedException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
