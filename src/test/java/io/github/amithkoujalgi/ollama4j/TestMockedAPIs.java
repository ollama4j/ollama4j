package io.github.amithkoujalgi.ollama4j;

import org.apache.hc.core5.http.ParseException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class TestMockedAPIs {
    @Test
    public void testMockPullModel() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        OllamaModel model = OllamaModel.LLAMA2;
        try {
            doNothing().when(ollamaAPI).pullModel(model);
            ollamaAPI.pullModel(model);
            verify(ollamaAPI, times(1)).pullModel(model);
        } catch (IOException | ParseException | OllamaBaseException e) {
            throw new RuntimeException(e);
        }
    }
}
