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
USER ASKS "WHO ARE YOU?" IT'S A REQUEST FOR IDENTITY. AS CHATGPT, WE SHOULD EXPLAIN THAT I'M AN AI DEVELOPED BY OPENAI, ETC. PROVIDE FRIENDLY EXPLANATION.
:::

:::tip[Response Tokens]
i’m chatgpt, a large language model created by openai. i’m designed to understand and generate natural‑language text, so i can answer questions, help with writing, explain concepts, brainstorm ideas, and chat about almost any topic. i don’t have a personal life or consciousness—i’m a tool that processes input and produces responses based on patterns in the data i was trained on. if you have any questions about how i work or what i can do, feel free to ask!
:::

### Generate response and receive the thinking and response tokens streamed

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/GenerateWithThinkingStreamed.java" />

You will get a response similar to:

:::tip[Thinking Tokens]
<TypewriterTextarea
textContent={`USER ASKS "WHO ARE YOU?" WE SHOULD EXPLAIN THAT I'M AN AI BY OPENAI, ETC.`}
typingSpeed={10}
pauseBetweenSentences={1200}
height="auto"
width="100%"
style={{ whiteSpace: 'pre-line' }}
/>
:::

:::tip[Response Tokens]
<TypewriterTextarea
textContent={`i’m chatgpt, a large language model created by openai.`}
typingSpeed={10}
pauseBetweenSentences={1200}
height="auto"
width="100%"
style={{ whiteSpace: 'pre-line' }}
/>
:::