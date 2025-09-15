package io.github.ollama4j.unittests;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.exceptions.RoleNotFoundException;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.embeddings.OllamaEmbedRequestModel;
import io.github.ollama4j.models.embeddings.OllamaEmbedResponseModel;
import io.github.ollama4j.models.request.CustomModelRequest;
import io.github.ollama4j.models.response.ModelDetail;
import io.github.ollama4j.models.response.OllamaAsyncResultStreamer;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.types.OllamaModelType;
import io.github.ollama4j.utils.OptionsBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
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
        CustomModelRequest customModelRequest = CustomModelRequest.builder().model("mario").from("llama3.2:latest").system("You are Mario from Super Mario Bros.").build();
        try {
            doNothing().when(ollamaAPI).createModel(customModelRequest);
            ollamaAPI.createModel(customModelRequest);
            verify(ollamaAPI, times(1)).createModel(customModelRequest);
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
    void testEmbed() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = OllamaModelType.LLAMA2;
        List<String> inputs = List.of("some prompt text");
        try {
            when(ollamaAPI.embed(model, inputs)).thenReturn(new OllamaEmbedResponseModel());
            ollamaAPI.embed(model, inputs);
            verify(ollamaAPI, times(1)).embed(model, inputs);
        } catch (IOException | OllamaBaseException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testEmbedWithEmbedRequestModel() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = OllamaModelType.LLAMA2;
        List<String> inputs = List.of("some prompt text");
        try {
            when(ollamaAPI.embed(new OllamaEmbedRequestModel(model, inputs))).thenReturn(new OllamaEmbedResponseModel());
            ollamaAPI.embed(new OllamaEmbedRequestModel(model, inputs));
            verify(ollamaAPI, times(1)).embed(new OllamaEmbedRequestModel(model, inputs));
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
            when(ollamaAPI.generate(model, prompt, false, false, optionsBuilder.build()))
                    .thenReturn(new OllamaResult("", "", 0, 200));
            ollamaAPI.generate(model, prompt, false, false, optionsBuilder.build());
            verify(ollamaAPI, times(1)).generate(model, prompt, false, false, optionsBuilder.build());
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
            when(ollamaAPI.generateWithImages(
                    model, prompt, Collections.emptyList(), new OptionsBuilder().build(), null, null))
                    .thenReturn(new OllamaResult("", "", 0, 200));
            ollamaAPI.generateWithImages(
                    model, prompt, Collections.emptyList(), new OptionsBuilder().build(), null, null);
            verify(ollamaAPI, times(1))
                    .generateWithImages(
                            model, prompt, Collections.emptyList(), new OptionsBuilder().build(), null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAskWithImageURLs() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = OllamaModelType.LLAMA2;
        String prompt = "some prompt text";
        try {
            when(ollamaAPI.generateWithImages(
                    model, prompt, Collections.emptyList(), new OptionsBuilder().build(), null, null))
                    .thenReturn(new OllamaResult("", "", 0, 200));
            ollamaAPI.generateWithImages(
                    model, prompt, Collections.emptyList(), new OptionsBuilder().build(), null, null);
            verify(ollamaAPI, times(1))
                    .generateWithImages(
                            model, prompt, Collections.emptyList(), new OptionsBuilder().build(), null, null);
        } catch (IOException | OllamaBaseException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAskAsync() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = OllamaModelType.LLAMA2;
        String prompt = "some prompt text";
        when(ollamaAPI.generate(model, prompt, false, false))
                .thenReturn(new OllamaAsyncResultStreamer(null, null, 3));
        ollamaAPI.generate(model, prompt, false, false);
        verify(ollamaAPI, times(1)).generate(model, prompt, false, false);
    }

    @Test
    void testAddCustomRole() {
        OllamaAPI ollamaAPI = mock(OllamaAPI.class);
        String roleName = "custom-role";
        OllamaChatMessageRole expectedRole = OllamaChatMessageRole.newCustomRole(roleName);
        when(ollamaAPI.addCustomRole(roleName)).thenReturn(expectedRole);
        OllamaChatMessageRole customRole = ollamaAPI.addCustomRole(roleName);
        assertEquals(expectedRole, customRole);
        verify(ollamaAPI, times(1)).addCustomRole(roleName);
    }

    @Test
    void testListRoles() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        OllamaChatMessageRole role1 = OllamaChatMessageRole.newCustomRole("role1");
        OllamaChatMessageRole role2 = OllamaChatMessageRole.newCustomRole("role2");
        List<OllamaChatMessageRole> expectedRoles = List.of(role1, role2);
        when(ollamaAPI.listRoles()).thenReturn(expectedRoles);
        List<OllamaChatMessageRole> actualRoles = ollamaAPI.listRoles();
        assertEquals(expectedRoles, actualRoles);
        verify(ollamaAPI, times(1)).listRoles();
    }

    @Test
    void testGetRoleNotFound() {
        OllamaAPI ollamaAPI = mock(OllamaAPI.class);
        String roleName = "non-existing-role";
        try {
            when(ollamaAPI.getRole(roleName)).thenThrow(new RoleNotFoundException("Role not found"));
        } catch (RoleNotFoundException exception) {
            throw new RuntimeException("Failed to run test: testGetRoleNotFound");
        }
        try {
            ollamaAPI.getRole(roleName);
            fail("Expected RoleNotFoundException not thrown");
        } catch (RoleNotFoundException exception) {
            assertEquals("Role not found", exception.getMessage());
        }
        try {
            verify(ollamaAPI, times(1)).getRole(roleName);
        } catch (RoleNotFoundException exception) {
            throw new RuntimeException("Failed to run test: testGetRoleNotFound");
        }
    }

    @Test
    void testGetRoleFound() {
        OllamaAPI ollamaAPI = mock(OllamaAPI.class);
        String roleName = "existing-role";
        OllamaChatMessageRole expectedRole = OllamaChatMessageRole.newCustomRole(roleName);
        try {
            when(ollamaAPI.getRole(roleName)).thenReturn(expectedRole);
        } catch (RoleNotFoundException exception) {
            throw new RuntimeException("Failed to run test: testGetRoleFound");
        }
        try {
            OllamaChatMessageRole actualRole = ollamaAPI.getRole(roleName);
            assertEquals(expectedRole, actualRole);
            verify(ollamaAPI, times(1)).getRole(roleName);
        } catch (RoleNotFoundException exception) {
            throw new RuntimeException("Failed to run test: testGetRoleFound");
        }
    }
}
