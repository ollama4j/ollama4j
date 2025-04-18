---
sidebar_position: 2
---

import CodeEmbed from '@site/src/components/CodeEmbed';

# List Local Models

This API lets you list downloaded/available models on the Ollama server.

<CodeEmbed
src='https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/ListLocalModels.java'>
</CodeEmbed>


If you have any models already downloaded on Ollama server, you would have them listed as follows:

```bash
llama2:latest
llama3.2:1b
qwen2:0.5b
qwen:0.5b
sqlcoder:latest
```