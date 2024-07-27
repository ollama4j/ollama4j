---
sidebar_position: 4
---

# PS

This API provides a list of running models and details about each model currently loaded into memory.

This API corresponds to the [PS](https://github.com/ollama/ollama/blob/main/docs/api.md#list-running-models) API.

```java
package io.github.ollama4j.localtests;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.ps.ModelsProcessResponse;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        OllamaAPI ollamaAPI = new OllamaAPI("http://localhost:11434");

        ModelsProcessResponse response = ollamaAPI.ps();

        System.out.println(response);
    }
}
```