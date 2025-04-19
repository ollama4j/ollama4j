---
sidebar_position: 1
---

import CodeEmbed from '@site/src/components/CodeEmbed';
import TypewriterTextarea from '@site/src/components/TypewriterTextarea';

# Generate (Sync)

This API lets you ask questions to the LLMs in a synchronous way.
This API corresponds to
the [completion](https://github.com/jmorganca/ollama/blob/main/docs/api.md#generate-a-completion) API.

Use the `OptionBuilder` to build the `Options` object
with [extra parameters](https://github.com/jmorganca/ollama/blob/main/docs/modelfile.md#valid-parameters-and-values).
Refer
to [this](/apis-extras/options-builder).

### Try asking a question about the model

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/Generate.java" />

You will get a response similar to:

::::tip[LLM Response]
> I am a large language model created by Alibaba Cloud. My purpose is to assist users in generating text, answering
> questions, and completing tasks. I aim to be user-friendly and easy to understand for everyone who interacts with me.
::::

### Try asking a question, receiving the answer streamed

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/GenerateStreamingWithTokenConcatenation.java" />

You will get a response similar to:

<!-- ::::tip[LLM Response]
> The
>
> The capital
>
> The capital of
>
> The capital of France
>
> The capital of France is
>
> The capital of France is Paris
>
> The capital of France is Paris.
:::: -->

<TypewriterTextarea
    textContent='The capital of France is Paris.'
    typingSpeed={30}
    pauseBetweenSentences={1200}
    height='55px'
    width='100%'
/>

## Generate structured output

### With response as a `Map`

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/StructuredOutput.java" />

You will get a response similar to:

::::tip[LLM Response]
```json
{
    "available": true,
    "age": 22
}
```
::::

### With response mapped to specified class type

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/StructuredOutputMappedToObject.java" />

::::tip[LLM Response]
Person(age=28, available=false)
::::