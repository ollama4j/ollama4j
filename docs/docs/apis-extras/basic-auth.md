---
sidebar_position: 2
---

# Set Basic Authentication

This API lets you set the basic authentication for the Ollama client. This would help in scenarios where
Ollama server would be setup behind a gateway/reverse proxy with basic auth.

After configuring basic authentication, all subsequent requests will include the Basic Auth header.

```java
import io.github.ollama4j.OllamaAPI;

public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        ollamaAPI.setBasicAuth("username", "password");
    }
}
```