---
sidebar_position: 4
---

# Bearer Auth

This API lets you set the bearer authentication for the Ollama client. This would help in scenarios where
Ollama server would be setup behind a gateway/reverse proxy with bearer auth.

After configuring bearer authentication, all subsequent requests will include the Bearer Auth header.

```java
import io.github.ollama4j.Ollama;

public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        Ollama ollama = new Ollama(host);

        ollama.setBearerAuth("YOUR-TOKEN");
    }
}
```