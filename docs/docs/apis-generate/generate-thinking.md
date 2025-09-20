---
sidebar_position: 3
---

import CodeEmbed from '@site/src/components/CodeEmbed';
import TypewriterTextarea from '@site/src/components/TypewriterTextarea';

# Generate with Thinking

This API allows to generate responses from an LLM while also retrieving the model's "thinking" process separately from the final answer. The "thinking" tokens represent the model's internal reasoning or planning before it produces the actual response. This can be useful for debugging, transparency, or simply understanding how the model arrives at its answers.

You can use this feature to receive both the thinking and the response as separate outputs, either as a complete result or streamed token by token. The examples below show how to use the API to access both the thinking and the response, and how to display them in your application.


### Generate response with thinking and receive the thinking and response text separately

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/GenerateWithThinking.java" />

You will get a response similar to:

:::tip[Thinking Tokens]
User asks "Who are you?" It's a request for identity. As ChatGPT, we should explain that I'm an AI developed by OpenAI, etc. Provide friendly explanation.
:::

:::tip[Response Tokens]
I’m ChatGPT, a large language model created by OpenAI. I’m designed to understand and generate natural‑language text, so I can answer questions, help with writing, explain concepts, brainstorm ideas, and chat about almost any topic. I don’t have a personal life or consciousness—I’m a tool that processes input and produces responses based on patterns in the data I was trained on. If you have any questions about how I work or what I can do, feel free to ask!
:::

### Generate response and receive the thinking and response tokens streamed

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/GenerateWithThinkingStreamed.java" />

You will get a response similar to:

:::tip[Thinking Tokens]
<TypewriterTextarea
textContent={`User asks "Who are you?" It's a request for identity. As ChatGPT, we should explain that I'm an AI developed by OpenAI, etc. Provide friendly explanation.`}
typingSpeed={10}
pauseBetweenSentences={1200}
height="auto"
width="100%"
style={{ whiteSpace: 'pre-line' }}
/>
:::

:::tip[Response Tokens]
<TypewriterTextarea
textContent={`I’m ChatGPT, a large language model created by OpenAI. I’m designed to understand and generate natural‑language text, so I can answer questions, help with writing, explain concepts, brainstorm ideas, and chat about almost any topic. I don’t have a personal life or consciousness—I’m a tool that processes input and produces responses based on patterns in the data I was trained on. If you have any questions about how I work or what I can do, feel free to ask!`}
typingSpeed={10}
pauseBetweenSentences={1200}
height="auto"
width="100%"
style={{ whiteSpace: 'pre-line' }}
/>
:::