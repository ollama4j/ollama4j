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
I am a model of an AI trained by Mistral AI. I was designed to assist with a wide range of tasks, from answering
questions to helping with complex computations and research. How can I help you toda
::::

### Try asking a question, receiving the answer streamed

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/GenerateStreaming.java" />

You will get a response similar to:

<TypewriterTextarea
textContent='The capital of France is Paris.'
typingSpeed={30}
pauseBetweenSentences={1200}
height='55px'
width='100%'
/>

## Generate structured output

### With response as a `Map`

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/GenerateStructuredOutput.java" />

You will get a response similar to:

::::tip[LLM Response]

```json
{
  "heroName" : "Batman",
  "ageOfPerson" : 30
}
```

::::

### With response mapped to specified class type

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/GenerateStructuredOutputMappedToObject.java" />

::::tip[LLM Response]
HeroInfo(heroName=Batman, ageOfPerson=30)
::::