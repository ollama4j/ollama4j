---
sidebar_position: 6
---

# Delete Model

This API lets you create a delete a model from the Ollama server.

```java title="DeleteModel.java"
import io.github.ollama4j.OllamaAPI;

public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        ollamaAPI.setVerbose(false);

        ollamaAPI.deleteModel("mycustommodel", true);
    }
}

```

Once deleted, you can verify it using [list models](./list-models) API.