/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import io.github.ollama4j.Ollama;
import io.github.ollama4j.exceptions.OllamaException;
import io.github.ollama4j.exceptions.RoleNotFoundException;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.embed.OllamaEmbedRequest;
import io.github.ollama4j.models.embed.OllamaEmbedResult;
import io.github.ollama4j.models.generate.OllamaGenerateRequest;
import io.github.ollama4j.models.generate.OllamaGenerateStreamObserver;
import io.github.ollama4j.models.request.CustomModelRequest;
import io.github.ollama4j.models.request.ThinkMode;
import io.github.ollama4j.models.response.ModelDetail;
import io.github.ollama4j.models.response.OllamaAsyncResultStreamer;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.utils.OptionsBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TestMockedAPIs {
    @Test
    void testPullModel() {
        Ollama ollama = Mockito.mock(Ollama.class);
        String model = "llama2";
        try {
            doNothing().when(ollama).pullModel(model);
            ollama.pullModel(model);
            verify(ollama, times(1)).pullModel(model);
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testListModels() {
        Ollama ollama = Mockito.mock(Ollama.class);
        try {
            when(ollama.listModels()).thenReturn(new ArrayList<>());
            ollama.listModels();
            verify(ollama, times(1)).listModels();
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCreateModel() {
        Ollama ollama = Mockito.mock(Ollama.class);
        CustomModelRequest customModelRequest =
                CustomModelRequest.builder()
                        .model("mario")
                        .from("llama3.2:latest")
                        .system("You are Mario from Super Mario Bros.")
                        .build();
        try {
            doNothing().when(ollama).createModel(customModelRequest);
            ollama.createModel(customModelRequest);
            verify(ollama, times(1)).createModel(customModelRequest);
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testDeleteModel() {
        Ollama ollama = Mockito.mock(Ollama.class);
        String model = "llama2";
        try {
            doNothing().when(ollama).deleteModel(model, true);
            ollama.deleteModel(model, true);
            verify(ollama, times(1)).deleteModel(model, true);
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetModelDetails() {
        Ollama ollama = Mockito.mock(Ollama.class);
        String model = "llama2";
        try {
            when(ollama.getModelDetails(model)).thenReturn(new ModelDetail());
            ollama.getModelDetails(model);
            verify(ollama, times(1)).getModelDetails(model);
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGenerateEmbeddings() {
        Ollama ollama = Mockito.mock(Ollama.class);
        String model = "llama2";
        String prompt = "some prompt text";
        try {
            OllamaEmbedRequest m = new OllamaEmbedRequest();
            m.setModel(model);
            m.setInput(List.of(prompt));
            when(ollama.embed(m)).thenReturn(new OllamaEmbedResult());
            ollama.embed(m);
            verify(ollama, times(1)).embed(m);
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testEmbed() {
        Ollama ollama = Mockito.mock(Ollama.class);
        String model = "llama2";
        List<String> inputs = List.of("some prompt text");
        try {
            OllamaEmbedRequest m = new OllamaEmbedRequest(model, inputs);
            when(ollama.embed(m)).thenReturn(new OllamaEmbedResult());
            ollama.embed(m);
            verify(ollama, times(1)).embed(m);
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testEmbedWithEmbedRequestModel() {
        Ollama ollama = Mockito.mock(Ollama.class);
        String model = "llama2";
        List<String> inputs = List.of("some prompt text");
        try {
            when(ollama.embed(new OllamaEmbedRequest(model, inputs)))
                    .thenReturn(new OllamaEmbedResult());
            ollama.embed(new OllamaEmbedRequest(model, inputs));
            verify(ollama, times(1)).embed(new OllamaEmbedRequest(model, inputs));
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAsk() {
        Ollama ollama = Mockito.mock(Ollama.class);
        String model = "llama2";
        String prompt = "some prompt text";
        OllamaGenerateStreamObserver observer = new OllamaGenerateStreamObserver(null, null);
        try {
            OllamaGenerateRequest request =
                    OllamaGenerateRequest.builder()
                            .withModel(model)
                            .withPrompt(prompt)
                            .withRaw(false)
                            .withThink(ThinkMode.DISABLED)
                            .withStreaming(false)
                            .build();
            when(ollama.generate(request, observer)).thenReturn(new OllamaResult("", "", 0, 200));
            ollama.generate(request, observer);
            verify(ollama, times(1)).generate(request, observer);
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAskWithImageFiles() {
        Ollama ollama = Mockito.mock(Ollama.class);
        String model = "llama2";
        String prompt = "some prompt text";
        try {
            OllamaGenerateRequest request =
                    OllamaGenerateRequest.builder()
                            .withModel(model)
                            .withPrompt(prompt)
                            .withRaw(false)
                            .withThink(ThinkMode.DISABLED)
                            .withStreaming(false)
                            .withImages(new ArrayList<>())
                            .withOptions(new OptionsBuilder().build())
                            .withFormat(null)
                            .build();
            OllamaGenerateStreamObserver handler = null;
            when(ollama.generate(request, handler)).thenReturn(new OllamaResult("", "", 0, 200));
            ollama.generate(request, handler);
            verify(ollama, times(1)).generate(request, handler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAskWithImageURLs() {
        Ollama ollama = Mockito.mock(Ollama.class);
        String model = "llama2";
        String prompt = "some prompt text";
        try {
            OllamaGenerateRequest request =
                    OllamaGenerateRequest.builder()
                            .withModel(model)
                            .withPrompt(prompt)
                            .withRaw(false)
                            .withThink(ThinkMode.DISABLED)
                            .withStreaming(false)
                            .withImages(new ArrayList<>())
                            .withOptions(new OptionsBuilder().build())
                            .withFormat(null)
                            .build();
            OllamaGenerateStreamObserver handler = null;
            when(ollama.generate(request, handler)).thenReturn(new OllamaResult("", "", 0, 200));
            ollama.generate(request, handler);
            verify(ollama, times(1)).generate(request, handler);
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAskAsync() throws OllamaException {
        Ollama ollama = Mockito.mock(Ollama.class);
        String model = "llama2";
        String prompt = "some prompt text";
        when(ollama.generateAsync(model, prompt, false, ThinkMode.DISABLED))
                .thenReturn(new OllamaAsyncResultStreamer(null, null, 3));
        ollama.generateAsync(model, prompt, false, ThinkMode.DISABLED);
        verify(ollama, times(1)).generateAsync(model, prompt, false, ThinkMode.DISABLED);
    }

    @Test
    void testAddCustomRole() {
        Ollama ollama = mock(Ollama.class);
        String roleName = "custom-role";
        OllamaChatMessageRole expectedRole = OllamaChatMessageRole.newCustomRole(roleName);
        when(ollama.addCustomRole(roleName)).thenReturn(expectedRole);
        OllamaChatMessageRole customRole = ollama.addCustomRole(roleName);
        assertEquals(expectedRole, customRole);
        verify(ollama, times(1)).addCustomRole(roleName);
    }

    @Test
    void testListRoles() {
        Ollama ollama = Mockito.mock(Ollama.class);
        OllamaChatMessageRole role1 = OllamaChatMessageRole.newCustomRole("role1");
        OllamaChatMessageRole role2 = OllamaChatMessageRole.newCustomRole("role2");
        List<OllamaChatMessageRole> expectedRoles = List.of(role1, role2);
        when(ollama.listRoles()).thenReturn(expectedRoles);
        List<OllamaChatMessageRole> actualRoles = ollama.listRoles();
        assertEquals(expectedRoles, actualRoles);
        verify(ollama, times(1)).listRoles();
    }

    @Test
    void testGetRoleNotFound() {
        Ollama ollama = mock(Ollama.class);
        String roleName = "non-existing-role";
        try {
            when(ollama.getRole(roleName)).thenThrow(new RoleNotFoundException("Role not found"));
        } catch (RoleNotFoundException exception) {
            throw new RuntimeException("Failed to run test: testGetRoleNotFound");
        }
        try {
            ollama.getRole(roleName);
            fail("Expected RoleNotFoundException not thrown");
        } catch (RoleNotFoundException exception) {
            assertEquals("Role not found", exception.getMessage());
        }
        try {
            verify(ollama, times(1)).getRole(roleName);
        } catch (RoleNotFoundException exception) {
            throw new RuntimeException("Failed to run test: testGetRoleNotFound");
        }
    }

    @Test
    void testGetRoleFound() {
        Ollama ollama = mock(Ollama.class);
        String roleName = "existing-role";
        OllamaChatMessageRole expectedRole = OllamaChatMessageRole.newCustomRole(roleName);
        try {
            when(ollama.getRole(roleName)).thenReturn(expectedRole);
        } catch (RoleNotFoundException exception) {
            throw new RuntimeException("Failed to run test: testGetRoleFound");
        }
        try {
            OllamaChatMessageRole actualRole = ollama.getRole(roleName);
            assertEquals(expectedRole, actualRole);
            verify(ollama, times(1)).getRole(roleName);
        } catch (RoleNotFoundException exception) {
            throw new RuntimeException("Failed to run test: testGetRoleFound");
        }
    }
}
