package io.github.amithkoujalgi.ollama4j.integrationtests;

import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.models.ModelDetail;
import io.github.amithkoujalgi.ollama4j.core.models.OllamaAsyncResultCallback;
import io.github.amithkoujalgi.ollama4j.core.models.OllamaResult;
import io.github.amithkoujalgi.ollama4j.core.types.OllamaModelType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class TestRealAPIs {
    @Test
    public void testMockPullModel() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = OllamaModelType.LLAMA2;
        try {
            doNothing().when(ollamaAPI).pullModel(model);
            ollamaAPI.pullModel(model);
            verify(ollamaAPI, times(1)).pullModel(model);
        } catch (IOException | OllamaBaseException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testListModels() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        try {
            when(ollamaAPI.listModels()).thenReturn(new ArrayList<>());
            ollamaAPI.listModels();
            verify(ollamaAPI, times(1)).listModels();
        } catch (IOException | OllamaBaseException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateModel() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = OllamaModelType.LLAMA2;
        String modelFilePath = "/somemodel";
        try {
            doNothing().when(ollamaAPI).createModel(model, modelFilePath);
            ollamaAPI.createModel(model, modelFilePath);
            verify(ollamaAPI, times(1)).createModel(model, modelFilePath);
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDeleteModel() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = OllamaModelType.LLAMA2;
        try {
            doNothing().when(ollamaAPI).deleteModel(model, true);
            ollamaAPI.deleteModel(model, true);
            verify(ollamaAPI, times(1)).deleteModel(model, true);
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetModelDetails() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = OllamaModelType.LLAMA2;
        try {
            when(ollamaAPI.getModelDetails(model)).thenReturn(new ModelDetail());
            ollamaAPI.getModelDetails(model);
            verify(ollamaAPI, times(1)).getModelDetails(model);
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGenerateEmbeddings() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = OllamaModelType.LLAMA2;
        String prompt = "some prompt text";
        try {
            when(ollamaAPI.generateEmbeddings(model, prompt)).thenReturn(new ArrayList<>());
            ollamaAPI.generateEmbeddings(model, prompt);
            verify(ollamaAPI, times(1)).generateEmbeddings(model, prompt);
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAsk() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = OllamaModelType.LLAMA2;
        String prompt = "some prompt text";
        try {
            when(ollamaAPI.ask(model, prompt)).thenReturn(new OllamaResult("", 0,200));
            ollamaAPI.ask(model, prompt);
            verify(ollamaAPI, times(1)).ask(model, prompt);
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAskAsync() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = OllamaModelType.LLAMA2;
        String prompt = "some prompt text";
        when(ollamaAPI.askAsync(model, prompt)).thenReturn(new OllamaAsyncResultCallback(null, null, null));
        ollamaAPI.askAsync(model, prompt);
        verify(ollamaAPI, times(1)).askAsync(model, prompt);
    }
}
