---
sidebar_position: 2
---

# Pull Model

This API lets you pull a model on the Ollama server.

```java title="PullModel.java"
public class Main {

    public static void main(String[] args) {
        
        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        ollamaAPI.pullModel(OllamaModelType.LLAMA2);
    }
}
```

Once downloaded, you can see them when you use [list models](./list-models) API.