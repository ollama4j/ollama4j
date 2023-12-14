package io.github.amithkoujalgi.ollama4j.integrationtests;

import static org.junit.jupiter.api.Assertions.*;

import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.types.OllamaModelType;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.net.http.HttpConnectTimeoutException;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

class TestRealAPIs {
  OllamaAPI ollamaAPI;

  private Properties loadProperties() {
    Properties properties = new Properties();
    try (InputStream input =
        getClass().getClassLoader().getResourceAsStream("test-config.properties")) {
      if (input == null) {
        throw new RuntimeException("Sorry, unable to find test-config.properties");
      }
      properties.load(input);
      return properties;
    } catch (IOException e) {
      throw new RuntimeException("Error loading properties", e);
    }
  }

  @BeforeEach
  void setUp() {
    Properties properties = loadProperties();
    ollamaAPI = new OllamaAPI(properties.getProperty("ollama.api.url"));
  }

  @Test
  @Order(1)
  void testWrongEndpoint() {
    OllamaAPI ollamaAPI = new OllamaAPI("http://wrong-host:11434");
    assertThrows(ConnectException.class, ollamaAPI::listModels);
  }

  @Test
  @Order(1)
  void testEndpointReachability() {
    try {
      assertNotNull(ollamaAPI.listModels());
    } catch (HttpConnectTimeoutException e) {
      fail(e.getMessage());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @Order(2)
  void testListModels() {
    testEndpointReachability();
    try {
      assertNotNull(ollamaAPI.listModels());
      ollamaAPI.listModels().forEach(System.out::println);
    } catch (IOException | OllamaBaseException | InterruptedException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @Order(2)
  void testPullModel() {
    testEndpointReachability();
    try {
      ollamaAPI.pullModel(OllamaModelType.LLAMA2);
      boolean found =
          ollamaAPI.listModels().stream()
              .anyMatch(model -> model.getModelName().equals(OllamaModelType.LLAMA2));
      assertTrue(found);
    } catch (IOException | OllamaBaseException | InterruptedException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
