---
sidebar_position: 1
---

import CodeEmbed from '@site/src/components/CodeEmbed';

# Models from Ollama Library

These API retrieves a list of models directly from the Ollama library.

### List Models from Ollama Library

This API fetches available models from the Ollama library page, including details such as the model's name, pull count,
popular tags, tag count, and the last update time.

<CodeEmbed
src='https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/ListLibraryModels.java'>
</CodeEmbed>

The following is the sample output:

```
[
    LibraryModel(name=llama3.2-vision, description=Llama 3.2 Vision is a collection of instruction-tuned image reasoning generative models in 11B and 90B sizes., pullCount=21.1K, totalTags=9, popularTags=[vision, 11b, 90b], lastUpdated=yesterday),
    LibraryModel(name=llama3.2, description=Meta's Llama 3.2 goes small with 1B and 3B models., pullCount=2.4M, totalTags=63, popularTags=[tools, 1b, 3b], lastUpdated=6 weeks ago)
]
```

### Get Tags of a Library Model

This API Fetches the tags associated with a specific model from Ollama library.

<CodeEmbed
src='https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/GetLibraryModelTags.java'>
</CodeEmbed>

The following is the sample output:

```
LibraryModelDetail(
  model=LibraryModel(name=llama3.2-vision, description=Llama 3.2 Vision is a collection of instruction-tuned image reasoning generative models in 11B and 90B sizes., pullCount=21.1K, totalTags=9, popularTags=[vision, 11b, 90b], lastUpdated=yesterday),
  tags=[
        LibraryModelTag(name=llama3.2-vision, tag=latest, size=7.9GB, lastUpdated=yesterday),
        LibraryModelTag(name=llama3.2-vision, tag=11b, size=7.9GB, lastUpdated=yesterday),
        LibraryModelTag(name=llama3.2-vision, tag=90b, size=55GB, lastUpdated=yesterday)
    ]
)
```

### Find a model from Ollama library

This API finds a specific model using model `name` and `tag` from Ollama library.

<CodeEmbed
src='https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/FindLibraryModel.java'>
</CodeEmbed>

The following is the sample output:

```
LibraryModelTag(name=qwen2.5, tag=7b, size=4.7GB, lastUpdated=7 weeks ago)
```

### Pull model using `LibraryModelTag`

You can use `LibraryModelTag` to pull models into Ollama server.

<CodeEmbed
src='https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/PullLibraryModelTags.java'>
</CodeEmbed>