---
sidebar_position: 3
---

import CodeEmbed from '@site/src/components/CodeEmbed';

# Generate with Image Files

This API lets you ask questions along with the image files to the LLMs.
This API corresponds to
the [completion](https://github.com/jmorganca/ollama/blob/main/docs/api.md#generate-a-completion) API.

:::note

Executing this on Ollama server running in CPU-mode will take longer to generate response. Hence, GPU-mode is
recommended.

:::

## Synchronous mode

If you have this image downloaded and you pass the path to the downloaded image to the following code:

![Img](https://t3.ftcdn.net/jpg/02/96/63/80/360_F_296638053_0gUVA4WVBKceGsIr7LNqRWSnkusi07dq.jpg)

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/GenerateWithImageFile.java" />

You will get a response similar to:

::::tip[LLM Response]
> This image features a white boat with brown cushions, where a dog is sitting on the back of the boat. The dog seems to
> be enjoying its time outdoors, perhaps on a lake.
::::