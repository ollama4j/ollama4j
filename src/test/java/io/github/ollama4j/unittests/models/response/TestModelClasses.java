/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.unittests.models.response;

import static org.junit.jupiter.api.Assertions.*;

import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.models.response.ModelDetail;
import io.github.ollama4j.models.response.ModelMeta;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class TestModelClasses {

    @Test
    void testModel() {
        Model model = new Model();
        model.setName("test-model:latest");
        model.setModifiedAt(OffsetDateTime.now());
        model.setSize(1000L);

        assertEquals("test-model:latest", model.getName());
        assertNotNull(model.getModifiedAt());
        assertEquals(1000L, model.getSize());
        assertEquals("test-model", model.getModelName());
        assertEquals("latest", model.getModelVersion());
    }

    @Test
    void testModelDetail() {
        ModelDetail detail = new ModelDetail();
        detail.setModelFile("modelfile content");
        detail.setParameters("parameters");
        detail.setTemplate("template");
        detail.setLicense("MIT");

        assertEquals("modelfile content", detail.getModelFile());
        assertEquals("parameters", detail.getParameters());
        assertEquals("template", detail.getTemplate());
        assertEquals("MIT", detail.getLicense());
    }

    @Test
    void testModelMeta() {
        ModelMeta meta = new ModelMeta();
        meta.setFormat("gguf");
        meta.setFamily("llama");
        meta.setParameterSize("7B");

        assertEquals("gguf", meta.getFormat());
        assertEquals("llama", meta.getFamily());
        assertEquals("7B", meta.getParameterSize());
    }
}
