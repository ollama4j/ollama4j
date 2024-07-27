---
sidebar_position: 1
---

# Set Verbosity

This API lets you set the verbosity of the Ollama client.

## Try asking a question about the model.

```java
import io.github.ollama4j.OllamaAPI;

public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        ollamaAPI.setVerbose(true);
    }
}
```