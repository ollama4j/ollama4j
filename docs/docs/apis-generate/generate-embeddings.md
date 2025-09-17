---
sidebar_position: 1
---

import CodeEmbed from '@site/src/components/CodeEmbed';

# Generate Embeddings

Generate embeddings from a model.

### Using `embed()`

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/GenerateEmbeddings.java" />

::::tip[LLM Response]

```json
[
  [
    0.010000081,
    -0.0017487297,
    0.050126992,
    0.04694895,
    0.055186987,
    0.008570699,
    0.10545243,
    -0.02591801,
    0.1296789,
  ],
  [
    -0.009868476,
    0.060335685,
    0.025288988,
    -0.0062160683,
    0.07281043,
    0.017217565,
    0.090314455,
    -0.051715206,
  ]
]
```

::::

You could also use the `OllamaEmbedRequestModel` to specify the options such as `seed`, `temperature`, etc., to apply
for generating embeddings.

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/GenerateEmbeddingsWithRequestModel.java" />

You will get a response similar to:

::::tip[LLM Response]

```json
[
  [
    0.010000081,
    -0.0017487297,
    0.050126992,
    0.04694895,
    0.055186987,
    0.008570699,
    0.10545243,
    -0.02591801,
    0.1296789,
  ],
  [
    -0.009868476,
    0.060335685,
    0.025288988,
    -0.0062160683,
    0.07281043,
    0.017217565,
    0.090314455,
    -0.051715206,
  ]
]
```

::::