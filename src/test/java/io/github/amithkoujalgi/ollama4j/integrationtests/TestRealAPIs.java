package io.github.amithkoujalgi.ollama4j.integrationtests;

import static org.junit.jupiter.api.Assertions.*;

import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.models.OllamaResult;
import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatMessage;
import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatMessageRole;
import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatRequestBuilder;
import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatRequestModel;
import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatResult;
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
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

class TestRealAPIs {
  OllamaAPI ollamaAPI;
  Config config;

  private File getImageFileFromClasspath(String fileName) {
    ClassLoader classLoader = getClass().getClassLoader();
    return new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
  }

  @BeforeEach
  void setUp() {
    config = new Config();
    ollamaAPI = new OllamaAPI(config.getOllamaURL());
    ollamaAPI.setRequestTimeoutSeconds(config.getRequestTimeoutSeconds());
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
      ollamaAPI.pullModel(config.getModel());
      boolean found =
          ollamaAPI.listModels().stream()
              .anyMatch(model -> model.getModel().equalsIgnoreCase(config.getModel()));
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
          ollamaAPI.generate(
              config.getModel(),
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
          ollamaAPI.generate(
              config.getModel(),
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
  void testChat() {
    testEndpointReachability();
    try {
      OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(config.getModel());
      OllamaChatRequestModel requestModel = builder.withMessage(OllamaChatMessageRole.USER, "What is the capital of France?")
             .withMessage(OllamaChatMessageRole.ASSISTANT, "Should be Paris!")
             .withMessage(OllamaChatMessageRole.USER,"And what is the second larges city?")
             .build();

      OllamaChatResult chatResult = ollamaAPI.chat(requestModel);
      assertNotNull(chatResult);
      assertFalse(chatResult.getResponse().isBlank());
      assertEquals(4,chatResult.getChatHistory().size());
    } catch (IOException | OllamaBaseException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @Order(3)
  void testChatWithSystemPrompt() {
    testEndpointReachability();
    try {
      OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(config.getModel());
      OllamaChatRequestModel requestModel = builder.withMessage(OllamaChatMessageRole.SYSTEM, "You are a silent bot that only says 'NI'. Do not say anything else under any circumstances!")
             .withMessage(OllamaChatMessageRole.USER,"What is the capital of France? And what's France's connection with Mona Lisa?")
             .build();

      OllamaChatResult chatResult = ollamaAPI.chat(requestModel);
      assertNotNull(chatResult);
      assertFalse(chatResult.getResponse().isBlank());
      assertTrue(chatResult.getResponse().startsWith("NI"));
      assertEquals(3,chatResult.getChatHistory().size());
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
          ollamaAPI.generateWithImageFiles(
              config.getImageModel(),
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
          ollamaAPI.generateWithImageURLs(
              config.getImageModel(),
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

@Data
class Config {
  private String ollamaURL;
  private String model;
  private String imageModel;
  private int requestTimeoutSeconds;

  public Config() {
    Properties properties = new Properties();
    try (InputStream input =
        getClass().getClassLoader().getResourceAsStream("test-config.properties")) {
      if (input == null) {
        throw new RuntimeException("Sorry, unable to find test-config.properties");
      }
      properties.load(input);
      this.ollamaURL = properties.getProperty("ollama.url");
      this.model = properties.getProperty("ollama.model");
      this.imageModel = properties.getProperty("ollama.model.image");
      this.requestTimeoutSeconds =
          Integer.parseInt(properties.getProperty("ollama.request-timeout-seconds"));
    } catch (IOException e) {
      throw new RuntimeException("Error loading properties", e);
    }
  }
}
