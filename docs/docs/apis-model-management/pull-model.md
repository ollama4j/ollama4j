---
sidebar_position: 3
---

# Pull Model

This API lets you pull a model on the Ollama server.

```java title="PullModel.java"
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.types.OllamaModelType;

public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        ollamaAPI.pullModel(OllamaModelType.LLAMA2);
    }
}
```

Once downloaded, you can see them when you use [list models](./list-models) API.

:::info

You can even pull models using Ollama model library APIs. This looks up the models directly on the Ollama model library page. Refer
to [this](./list-library-models#pull-model-using-librarymodeltag).

:::

