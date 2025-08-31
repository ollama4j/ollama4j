---
sidebar_position: 8
---

import CodeEmbed from '@site/src/components/CodeEmbed';
import TypewriterTextarea from '@site/src/components/TypewriterTextarea';

# Chat with Thinking

This API allows to generate responses from an LLM while also retrieving the model's "thinking" process separately from
the final answer. The "thinking" tokens represent the model's internal reasoning or planning before it produces the
actual response. This can be useful for debugging, transparency, or simply understanding how the model arrives at its
answers.

You can use this feature to receive both the thinking and the response as separate outputs, either as a complete result
or streamed token by token. The examples below show how to use the API to access both the thinking and the response, and
how to display them in your application.

### Chat with thinking model and receive the thinking and response text separately

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/ChatWithThinkingModelExample.java" />

You will get a response similar to:

:::tip[LLM Response]
**First thinking response:** User asks a simple question. We just answer.

**First answer response:** The capital of France is _**Paris**_.

**Second thinking response:** User: "And what is the second largest city?" They asked about the second largest city in
France. Provide answer: Paris largest, second largest is Marseille. We can provide population stats, maybe mention Lyon
as third largest. Also context. The answer should be concise. Provide some details: Marseille is the second largest,
population ~870k, located on Mediterranean coast. Provide maybe some facts. Given no request for extra context, just answer.

**Second answer response:** The second‑largest city in France is _**Marseille**_. It’s a major Mediterranean port with a
population of roughly 870,000 (as of the latest estimates) and is known for its historic Old Port, vibrant cultural
scene, and diverse population.
:::

### Chat with thinking model and receive the thinking and response tokens streamed

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/ChatStreamingWithThinkingExample.java" />

You will get a response similar to:

:::tip[First Question's Thinking Tokens]
<TypewriterTextarea
textContent={`USER ASKS A SIMPLE QUESTION: "WHAT IS THE CAPITAL OF FRANCE?" THE ANSWER: PARIS. PROVIDE ANSWER.`}
typingSpeed={10}
pauseBetweenSentences={1200}
height="auto"
width="100%"
style={{ whiteSpace: 'pre-line' }}
/>
:::

:::tip[First Question's Response Tokens]
<TypewriterTextarea
textContent={`the capital of france is 'paris'.`}
typingSpeed={10}
pauseBetweenSentences={1200}
height="auto"
width="100%"
style={{ whiteSpace: 'pre-line' }}
/>
:::

:::tip[Second Question's Thinking Tokens]
<TypewriterTextarea
textContent={`THE USER ASKS: "AND WHAT IS THE SECOND LARGEST CITY?" LIKELY REFERRING TO FRANCE. THE SECOND LARGEST CITY IN FRANCE (BY POPULATION) IS MARSEILLE. HOWEVER, THERE MIGHT BE NUANCE: THE LARGEST IS PARIS, SECOND LARGEST IS MARSEILLE. BUT SOME MIGHT ARGUE THAT LYON IS SECOND LARGEST? LET'S CONFIRM: POPULATION OF FRANCE: PARIS ~2.1M (METRO 12M). MARSEILLE ~870K (METRO 1.5M). LYON ~515K (METRO 1.5M). SO MARSEILLE IS SECOND LARGEST CITY PROPER. LYON IS THIRD LARGEST. SO ANSWER: MARSEILLE. WE SHOULD PROVIDE THAT. PROVIDE A BRIEF EXPLANATION.`}
typingSpeed={10}
pauseBetweenSentences={1200}
height="auto"
width="100%"
style={{ whiteSpace: 'pre-line' }}
/>
:::

:::tip[Second Question's Response Tokens]
<TypewriterTextarea
textContent={`the second‑largest city in france by population is 'marseille'.
- marseille ≈ 870,000 residents (city proper)
- lyon ≈ 515,000 residents (city proper)

so marseille comes after paris as france’s largest city.`}
typingSpeed={10}
pauseBetweenSentences={1200}
height="auto"
width="100%"
style={{ whiteSpace: 'pre-line' }}
/>
:::