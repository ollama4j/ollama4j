---
sidebar_position: 2
---

# Timeouts

### Set Request Timeout

This API lets you set the request timeout for the Ollama client.

```java
import io.github.ollama4j.Ollama;
import io.github.ollama4j.OllamaAPI;

public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        Ollama ollama = new Ollama(host);

        ollama.setRequestTimeoutSeconds(10);
    }
}
```