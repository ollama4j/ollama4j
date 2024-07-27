---
sidebar_position: 3
---

# Ping

This API lets you check the reachability of Ollama server.

```java
import io.github.ollama4j.OllamaAPI;

public class Main {

    public static void main(String[] args) {
        String host = "http://localhost:11434/";
        
        OllamaAPI ollamaAPI = new OllamaAPI(host);
        
        ollamaAPI.ping();
    }
}
```