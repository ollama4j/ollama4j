---
sidebar_position: 2
---

import CodeEmbed from '@site/src/components/CodeEmbed';

# Generate (Async)

This API lets you ask questions to the LLMs in a asynchronous way.
This is particularly helpful when you want to issue a generate request to the LLM and collect the response in the
background (such as threads) without blocking your code until the response arrives from the model.

This API corresponds to
the [completion](https://github.com/jmorganca/ollama/blob/main/docs/api.md#generate-a-completion) API.

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/GenerateAsync.java" />

::::tip[LLM Response]
Here are the participating teams in the 2019 ICC Cricket World Cup:

1. Australia
2. Bangladesh
3. India
4. New Zealand
5. Pakistan
6. England
7. South Africa
8. West Indies (as a team)
9. Afghanistan
::::