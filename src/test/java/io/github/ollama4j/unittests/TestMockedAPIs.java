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

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaException;
import io.github.ollama4j.exceptions.RoleNotFoundException;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.embed.OllamaEmbedRequestModel;
import io.github.ollama4j.models.embed.OllamaEmbedResponseModel;
import io.github.ollama4j.models.generate.OllamaGenerateRequest;
import io.github.ollama4j.models.generate.OllamaGenerateRequestBuilder;
import io.github.ollama4j.models.generate.OllamaGenerateStreamObserver;
import io.github.ollama4j.models.request.CustomModelRequest;
import io.github.ollama4j.models.response.ModelDetail;
import io.github.ollama4j.models.response.OllamaAsyncResultStreamer;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.utils.OptionsBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TestMockedAPIs {
    @Test
    void testPullModel() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = "llama2";
        try {
            doNothing().when(ollamaAPI).pullModel(model);
            ollamaAPI.pullModel(model);
            verify(ollamaAPI, times(1)).pullModel(model);
        } catch (OllamaException e) {
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
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCreateModel() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        CustomModelRequest customModelRequest =
                CustomModelRequest.builder()
                        .model("mario")
                        .from("llama3.2:latest")
                        .system("You are Mario from Super Mario Bros.")
                        .build();
        try {
            doNothing().when(ollamaAPI).createModel(customModelRequest);
            ollamaAPI.createModel(customModelRequest);
            verify(ollamaAPI, times(1)).createModel(customModelRequest);
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testDeleteModel() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = "llama2";
        try {
            doNothing().when(ollamaAPI).deleteModel(model, true);
            ollamaAPI.deleteModel(model, true);
            verify(ollamaAPI, times(1)).deleteModel(model, true);
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        }
    }

    //    @Test
    //    void testRegisteredTools() {
    //        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
    //        doNothing().when(ollamaAPI).registerTools(Collections.emptyList());
    //        ollamaAPI.registerTools(Collections.emptyList());
    //        verify(ollamaAPI, times(1)).registerTools(Collections.emptyList());
    //
    //        List<Tools.ToolSpecification> toolSpecifications = new ArrayList<>();
    //        toolSpecifications.add(getSampleToolSpecification());
    //        doNothing().when(ollamaAPI).registerTools(toolSpecifications);
    //        ollamaAPI.registerTools(toolSpecifications);
    //        verify(ollamaAPI, times(1)).registerTools(toolSpecifications);
    //    }

    @Test
    void testGetModelDetails() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = "llama2";
        try {
            when(ollamaAPI.getModelDetails(model)).thenReturn(new ModelDetail());
            ollamaAPI.getModelDetails(model);
            verify(ollamaAPI, times(1)).getModelDetails(model);
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGenerateEmbeddings() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = "llama2";
        String prompt = "some prompt text";
        try {
            OllamaEmbedRequestModel m = new OllamaEmbedRequestModel();
            m.setModel(model);
            m.setInput(List.of(prompt));
            when(ollamaAPI.embed(m)).thenReturn(new OllamaEmbedResponseModel());
            ollamaAPI.embed(m);
            verify(ollamaAPI, times(1)).embed(m);
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testEmbed() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = "llama2";
        List<String> inputs = List.of("some prompt text");
        try {
            OllamaEmbedRequestModel m = new OllamaEmbedRequestModel(model, inputs);
            when(ollamaAPI.embed(m)).thenReturn(new OllamaEmbedResponseModel());
            ollamaAPI.embed(m);
            verify(ollamaAPI, times(1)).embed(m);
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testEmbedWithEmbedRequestModel() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = "llama2";
        List<String> inputs = List.of("some prompt text");
        try {
            when(ollamaAPI.embed(new OllamaEmbedRequestModel(model, inputs)))
                    .thenReturn(new OllamaEmbedResponseModel());
            ollamaAPI.embed(new OllamaEmbedRequestModel(model, inputs));
            verify(ollamaAPI, times(1)).embed(new OllamaEmbedRequestModel(model, inputs));
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAsk() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = "llama2";
        String prompt = "some prompt text";
        OptionsBuilder optionsBuilder = new OptionsBuilder();
        OllamaGenerateStreamObserver observer = new OllamaGenerateStreamObserver(null, null);
        try {
            OllamaGenerateRequest request =
                    OllamaGenerateRequestBuilder.builder()
                            .withModel(model)
                            .withPrompt(prompt)
                            .withRaw(false)
                            .withThink(false)
                            .withStreaming(false)
                            .build();
            when(ollamaAPI.generate(request, observer))
                    .thenReturn(new OllamaResult("", "", 0, 200));
            ollamaAPI.generate(request, observer);
            verify(ollamaAPI, times(1)).generate(request, observer);
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAskWithImageFiles() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = "llama2";
        String prompt = "some prompt text";
        try {
            OllamaGenerateRequest request =
                    OllamaGenerateRequestBuilder.builder()
                            .withModel(model)
                            .withPrompt(prompt)
                            .withRaw(false)
                            .withThink(false)
                            .withStreaming(false)
                            .withImages(Collections.emptyList())
                            .withOptions(new OptionsBuilder().build())
                            .withFormat(null)
                            .build();
            OllamaGenerateStreamObserver handler = null;
            when(ollamaAPI.generate(request, handler)).thenReturn(new OllamaResult("", "", 0, 200));
            ollamaAPI.generate(request, handler);
            verify(ollamaAPI, times(1)).generate(request, handler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAskWithImageURLs() {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = "llama2";
        String prompt = "some prompt text";
        try {
            OllamaGenerateRequest request =
                    OllamaGenerateRequestBuilder.builder()
                            .withModel(model)
                            .withPrompt(prompt)
                            .withRaw(false)
                            .withThink(false)
                            .withStreaming(false)
                            .withImages(Collections.emptyList())
                            .withOptions(new OptionsBuilder().build())
                            .withFormat(null)
                            .build();
            OllamaGenerateStreamObserver handler = null;
            when(ollamaAPI.generate(request, handler)).thenReturn(new OllamaResult("", "", 0, 200));
            ollamaAPI.generate(request, handler);
            verify(ollamaAPI, times(1)).generate(request, handler);
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAskAsync() throws OllamaException {
        OllamaAPI ollamaAPI = Mockito.mock(OllamaAPI.class);
        String model = "llama2";
        String prompt = "some prompt text";
        when(ollamaAPI.generateAsync(model, prompt, false, false))
                .thenReturn(new OllamaAsyncResultStreamer(null, null, 3));
        ollamaAPI.generateAsync(model, prompt, false, false);
        verify(ollamaAPI, times(1)).generateAsync(model, prompt, false, false);
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
            when(ollamaAPI.getRole(roleName))
                    .thenThrow(new RoleNotFoundException("Role not found"));
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

    //    private static Tools.ToolSpecification getSampleToolSpecification() {
    //        return Tools.ToolSpecification.builder()
    //                .functionName("current-weather")
    //                .functionDescription("Get current weather")
    //                .toolFunction(
    //                        new ToolFunction() {
    //                            @Override
    //                            public Object apply(Map<String, Object> arguments) {
    //                                String location = arguments.get("city").toString();
    //                                return "Currently " + location + "'s weather is beautiful.";
    //                            }
    //                        })
    //                .toolPrompt(
    //                        Tools.PromptFuncDefinition.builder()
    //                                .type("prompt")
    //                                .function(
    //                                        Tools.PromptFuncDefinition.PromptFuncSpec.builder()
    //                                                .name("get-location-weather-info")
    //                                                .description("Get location details")
    //                                                .parameters(
    //                                                        Tools.PromptFuncDefinition.Parameters
    //                                                                .builder()
    //                                                                .type("object")
    //                                                                .properties(
    //                                                                        Map.of(
    //                                                                                "city",
    //                                                                                Tools
    //
    // .PromptFuncDefinition
    //
    // .Property
    //
    // .builder()
    //                                                                                        .type(
    //
    //  "string")
    //
    // .description(
    //
    //  "The city,"
    //
    //      + " e.g."
    //
    //      + " New Delhi,"
    //
    //      + " India")
    //
    // .required(
    //
    //  true)
    //
    // .build()))
    //
    // .required(java.util.List.of("city"))
    //                                                                .build())
    //                                                .build())
    //                                .build())
    //                .build();
    //    }
}
