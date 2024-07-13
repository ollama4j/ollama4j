package io.github.amithkoujalgi.ollama4j.unittests;

import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.models.ModelDetail;
import io.github.amithkoujalgi.ollama4j.core.models.OllamaAsyncResultCallback;
import io.github.amithkoujalgi.ollama4j.core.models.OllamaResult;
import io.github.amithkoujalgi.ollama4j.core.types.OllamaModelType;
import io.github.amithkoujalgi.ollama4j.core.utils.OptionsBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.Mockito.*;

class TestMockedAPIs {
    @Test
    void testPullModel() {
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
    void testListModels() {
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
    void testCreateModel() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = OllamaModelType.LLAMA2;
        String modelFilePath = "FROM llama2\nSYSTEM You are mario from Super Mario Bros.";
        try {
            doNothing().when(ollamaAPI).createModelWithModelFileContents(model, modelFilePath);
            ollamaAPI.createModelWithModelFileContents(model, modelFilePath);
            verify(ollamaAPI, times(1)).createModelWithModelFileContents(model, modelFilePath);
        } catch (IOException | OllamaBaseException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testDeleteModel() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = OllamaModelType.LLAMA2;
        try {
            doNothing().when(ollamaAPI).deleteModel(model, true);
            ollamaAPI.deleteModel(model, true);
            verify(ollamaAPI, times(1)).deleteModel(model, true);
        } catch (IOException | OllamaBaseException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetModelDetails() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = OllamaModelType.LLAMA2;
        try {
            when(ollamaAPI.getModelDetails(model)).thenReturn(new ModelDetail());
            ollamaAPI.getModelDetails(model);
            verify(ollamaAPI, times(1)).getModelDetails(model);
        } catch (IOException | OllamaBaseException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGenerateEmbeddings() {
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
    void testAsk() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = OllamaModelType.LLAMA2;
        String prompt = "some prompt text";
        OptionsBuilder optionsBuilder = new OptionsBuilder();
        try {
            when(ollamaAPI.generate(model, prompt, false, optionsBuilder.build()))
                    .thenReturn(new OllamaResult("", 0, 200));
            ollamaAPI.generate(model, prompt, false, optionsBuilder.build());
            verify(ollamaAPI, times(1)).generate(model, prompt, false, optionsBuilder.build());
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAskWithImageFiles() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = OllamaModelType.LLAMA2;
        String prompt = "some prompt text";
        try {
            when(ollamaAPI.generateWithImageFiles(
                    model, prompt, Collections.emptyList(), new OptionsBuilder().build()))
                    .thenReturn(new OllamaResult("", 0, 200));
            ollamaAPI.generateWithImageFiles(
                    model, prompt, Collections.emptyList(), new OptionsBuilder().build());
            verify(ollamaAPI, times(1))
                    .generateWithImageFiles(
                            model, prompt, Collections.emptyList(), new OptionsBuilder().build());
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAskWithImageURLs() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = OllamaModelType.LLAMA2;
        String prompt = "some prompt text";
        try {
            when(ollamaAPI.generateWithImageURLs(
                    model, prompt, Collections.emptyList(), new OptionsBuilder().build()))
                    .thenReturn(new OllamaResult("", 0, 200));
            ollamaAPI.generateWithImageURLs(
                    model, prompt, Collections.emptyList(), new OptionsBuilder().build());
            verify(ollamaAPI, times(1))
                    .generateWithImageURLs(
                            model, prompt, Collections.emptyList(), new OptionsBuilder().build());
        } catch (IOException | OllamaBaseException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAskAsync() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = OllamaModelType.LLAMA2;
        String prompt = "some prompt text";
        when(ollamaAPI.generateAsync(model, prompt, false))
                .thenReturn(new OllamaAsyncResultCallback(null, null, 3));
        ollamaAPI.generateAsync(model, prompt, false);
        verify(ollamaAPI, times(1)).generateAsync(model, prompt, false);
    }
}
