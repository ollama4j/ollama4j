---
sidebar_position: 8
---

# Using with llmman

[llmman](https://github.com/llmmanorg/llmman) is a local model runner that serves the Ollama API (alongside OpenAI- and
Anthropic-compatible ones) on port `17434`. Models are pulled as OCI artifacts or straight from Hugging Face
(`hf.co/org/model`) and served by `llama.cpp`, `vllm` or `mlx-lm`.

Because llmman speaks the same API, Ollama4j works with it unchanged. The only difference from Ollama is the port.

### Start llmman

```bash
curl -fsSL https://raw.githubusercontent.com/llmmanorg/llmman/main/install.sh | sh
llmman pull gemma4
llmman serve
```

The server listens on `http://localhost:17434` by default (override with the `LLMMAN_HOST` environment variable).

### Point Ollama4j at it

```java
import io.github.ollama4j.Ollama;

public class Main {

    public static void main(String[] args) {
        String host = "http://localhost:17434/";

        Ollama ollama = new Ollama(host);

        ollama.ping();
    }
}
```
