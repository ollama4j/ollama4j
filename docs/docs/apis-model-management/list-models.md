---
sidebar_position: 1
---

# List Models

This API lets you list available models on the Ollama server.

```java title="ListModels.java"
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.Model;

import java.util.List;

public class ListModels {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        List<Model> models = ollamaAPI.listModels();

        models.forEach(model -> System.out.println(model.getName()));
    }
}
```

If you have any models already downloaded on Ollama server, you would have them listed as follows:

```bash
llama2:latest
sqlcoder:latest
```