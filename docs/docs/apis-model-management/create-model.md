---
sidebar_position: 4
---

# Create Model

This API lets you create a custom model on the Ollama server.

```java title="CreateModel.java"
public class CreateModel {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        ollamaAPI.createModel("mycustommodel", "/path/to/modelfile/on/ollama-server");
    }
}
```

Once created, you can see it when you use [list models](./list-models) API.