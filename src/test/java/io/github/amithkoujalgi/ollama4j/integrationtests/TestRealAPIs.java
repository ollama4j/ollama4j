package io.github.amithkoujalgi.ollama4j.integrationtests;

import static org.junit.jupiter.api.Assertions.*;

import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.models.OllamaResult;
import io.github.amithkoujalgi.ollama4j.core.types.OllamaModelType;
import io.github.amithkoujalgi.ollama4j.core.utils.OptionsBuilder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.net.http.HttpConnectTimeoutException;
import java.util.List;
import java.util.Objects;
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

  private File getImageFileFromClasspath(String fileName) {
    ClassLoader classLoader = getClass().getClassLoader();
    return new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
  }

  @BeforeEach
  void setUp() {
    Properties properties = loadProperties();
    ollamaAPI = new OllamaAPI(properties.getProperty("ollama.api.url"));
    ollamaAPI.setRequestTimeoutSeconds(20);
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

  @Test
  @Order(3)
  void testAskModelWithDefaultOptions() {
    testEndpointReachability();
    try {
      OllamaResult result =
          ollamaAPI.ask(
              OllamaModelType.LLAMA2,
              "What is the capital of France? And what's France's connection with Mona Lisa?",
              new OptionsBuilder().build());
      assertNotNull(result);
      assertNotNull(result.getResponse());
      assertFalse(result.getResponse().isEmpty());
    } catch (IOException | OllamaBaseException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @Order(3)
  void testAskModelWithOptions() {
    testEndpointReachability();
    try {
      OllamaResult result =
          ollamaAPI.ask(
              OllamaModelType.LLAMA2,
              "What is the capital of France? And what's France's connection with Mona Lisa?",
              new OptionsBuilder().setTemperature(0.9f).build());
      assertNotNull(result);
      assertNotNull(result.getResponse());
      assertFalse(result.getResponse().isEmpty());
    } catch (IOException | OllamaBaseException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @Order(3)
  void testAskModelWithOptionsAndImageFiles() {
    testEndpointReachability();
    File imageFile = getImageFileFromClasspath("dog-on-a-boat.jpg");
    try {
      OllamaResult result =
          ollamaAPI.askWithImageFiles(
              OllamaModelType.LLAVA,
              "What is in this image?",
              List.of(imageFile),
              new OptionsBuilder().build());
      assertNotNull(result);
      assertNotNull(result.getResponse());
      assertFalse(result.getResponse().isEmpty());
    } catch (IOException | OllamaBaseException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @Order(3)
  void testAskModelWithOptionsAndImageURLs() {
    testEndpointReachability();
    try {
      OllamaResult result =
          ollamaAPI.askWithImageURLs(
              OllamaModelType.LLAVA,
              "What is in this image?",
              List.of(
                  "https://t3.ftcdn.net/jpg/02/96/63/80/360_F_296638053_0gUVA4WVBKceGsIr7LNqRWSnkusi07dq.jpg"),
              new OptionsBuilder().build());
      assertNotNull(result);
      assertNotNull(result.getResponse());
      assertFalse(result.getResponse().isEmpty());
    } catch (IOException | OllamaBaseException | InterruptedException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
