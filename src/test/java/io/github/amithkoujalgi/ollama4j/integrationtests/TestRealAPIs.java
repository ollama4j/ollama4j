package io.github.amithkoujalgi.ollama4j.integrationtests;

import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.models.ModelDetail;
import io.github.amithkoujalgi.ollama4j.core.models.OllamaAsyncResultCallback;
import io.github.amithkoujalgi.ollama4j.core.models.OllamaResult;
import io.github.amithkoujalgi.ollama4j.core.types.OllamaModelType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class TestRealAPIs {

  OllamaAPI ollamaAPI;

  @BeforeEach
  void setUp() {
    String ollamaHost = "http://localhost:11434";
    ollamaAPI = new OllamaAPI(ollamaHost);
  }

  @Test
  void testListModels() {
    try {
      assertNotNull(ollamaAPI.listModels());
    } catch (IOException | OllamaBaseException | InterruptedException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
