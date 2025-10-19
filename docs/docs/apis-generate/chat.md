---
sidebar_position: 7
---

import CodeEmbed from '@site/src/components/CodeEmbed';
import TypewriterTextarea from '@site/src/components/TypewriterTextarea';

# Chat

This API lets you create a conversation with LLMs. Using this API enables you to ask questions to the model including
information using the history of already asked questions and the respective answers.

### Create a new conversation and use chat history to augment follow up questions

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/ChatExample.java" />

You will get a response similar to:

:::tip[LLM Response]

> First answer: The capital of France is Paris.
>
> Second answer: The second-largest city in France is Marseille.
>
> Chat History:

```json
[{
  "role" : "user",
  "content" : "What is the capital of France?",
  "images" : null,
  "tool_calls" : [ ]
}, {
  "role" : "assistant",
  "content" : "The capital of France is Paris.",
  "images" : null,
  "tool_calls" : null
}, {
  "role" : "user",
  "content" : "And what is the second largest city?",
  "images" : null,
  "tool_calls" : [ ]
}, {
  "role" : "assistant",
  "content" : "The second-largest city in France is Marseille.",
  "images" : null,
  "tool_calls" : null
}]
```
:::

### Create a conversation where the answer is streamed

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/ChatStreamingExample.java" />

<TypewriterTextarea
    textContent="'The Great Gatsby' by F. Scott Fitzgerald is a complex and multifaceted novel that explores themes of wealth, class, love, loss, and the American Dream. It is a landmark work of American literature that examines the social and psychological consequences of the American Dream's unattainability and its impact on the lives of its characters."
    typingSpeed={5}
    pauseBetweenSentences={1200}
    height='140px'
    width='100%'
/>

### Using a simple Console Output Stream Handler

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/ChatWithConsoleHandlerExample.java" />

### With a Stream Handler to receive the tokens as they are generated

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/ChatStreamingExample.java" />

### Create a new conversation with custom system prompt

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/ChatWithCustomSystemPrompt.java" />

You will get a response as:

:::tip[LLM Response]
Shhh!
:::


## Create a conversation about an image (requires a vision model)

Let's use this image:

<img src="https://t3.ftcdn.net/jpg/02/96/63/80/360_F_296638053_0gUVA4WVBKceGsIr7LNqRWSnkusi07dq.jpg" alt="Img" style={{ maxWidth: '250px', height: 'auto', display: 'block', margin: '1rem 0' }} />

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/ChatWithImage.java" />


You will get a response similar to:

:::tip[LLM Response]
**First Answer:** The image shows a dog sitting on the bow of a boat that is docked in calm water. The boat has two
levels, with the lower level containing seating and what appears to be an engine cover. The dog seems relaxed and
comfortable on the boat, looking out over the water. The background suggests it might be late afternoon or early
evening, given the warm lighting and the low position of the sun in the sky.

**Second Answer:** Based on the image, it's difficult to definitively determine the breed of the dog. However, the dog
appears to be medium-sized with a short coat and a brown coloration, which might suggest that it is a **_Golden Retriever_**
or a similar breed. Without more details like ear shape and tail length, it's not possible to identify the exact breed
confidently.
:::
