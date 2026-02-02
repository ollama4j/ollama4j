/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2026 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j;

import io.github.ollama4j.exceptions.OllamaException;
import io.github.ollama4j.models.generate.OllamaGenerateImageRequest;
import io.github.ollama4j.models.response.OllamaImageResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

public class Imagen {
    public static void main(String[] args) throws OllamaException, IOException {
        Ollama ollama = new Ollama("http://localhost:11434");
        OllamaGenerateImageRequest request = new OllamaGenerateImageRequest();
        request.setModel("x/z-image-turbo");
        request.setPrompt(
                "A white boat with brown cushions, where a dog is sitting on the back of the boat."
                        + " The dog seems to be enjoying its time outdoors, perhaps on a lake.");
        request.setWidth(512);
        request.setHeight(512);
        OllamaImageResult result = ollama.generateImage(request);
        Path outputPath = Paths.get("/Users/amith.koujalgi/Desktop/result.png");
        byte[] imageBytes = Base64.getDecoder().decode(result.getImage());
        Files.write(outputPath, imageBytes, StandardOpenOption.CREATE);
    }
}
