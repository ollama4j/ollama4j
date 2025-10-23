---
sidebar_position: 4

title: Agents 🆕
---

import CodeEmbed from '@site/src/components/CodeEmbed';
import TypewriterTextarea from '@site/src/components/TypewriterTextarea';

# Agents

An **agent** is an intelligent assistant that understands user requests, communicates using LLMs, and performs actions by invoking appropriate tools (exposed as code).

With agents, you can:
- Orchestrate multi-step reasoning and tool use (e.g., answering questions, looking up data, making reservations, sending emails, and more)
- Automatically select and execute the right tools or actions based on user intent
- Maintain conversation context to support dynamic, interactive problem solving
- Adapt behavior, persona, or expertise by simply changing configuration—without changing your Java code

Agents help by acting as an intelligent bridge between users, LLMs, and your application's capabilities. They can automate tasks, provide personalized assistance, and extend what LLMs can do by calling your Java methods or integrating with external systems.

With Ollama4j, creating an agent is as simple as describing its purpose, available tools, behavior, and preferred language model—all defined in a single YAML file.

**Why consider building agents using Ollama4j?**

- **Seamless Customization:** Effortlessly fine-tune your agent's personality, expertise, or workflow by editing the YAML—no need to recompile or modify your Java code.
- **Plug-and-Play Extensibility:** Add new tools or swap out existing logic classes without wrestling with framework internals or glue code.
- **Rapid Iteration:** Experiment freely. Try different models, instructions, and toolsets to try new behaviors or orchestrations in minutes.
- **Clear Separation of Concerns:** Keep your core business logic (Java) and conversational configuration (YAML) distinct, promoting clarity, maintainability, and collaboration.

---

### Define an Agent in YAML

Specify everything about your agent—what LLM it uses, its “personality,” and all callable tools—in a single YAML file.

**Agent configuration parameters:**

| Field                   | Description                                                                                    |
|-------------------------|------------------------------------------------------------------------------------------------|
| `name`                  | Name of your agent.                                                                            |
| `host`                  | The base URL for your Ollama server (e.g., `http://localhost:11434`).                          |
| `model`                 | The LLM backing your agent (e.g., `llama3`, `gemma`, `mistral`, etc).                          |
| `customPrompt`          | _(optional)_ System prompt—instructions or persona for your agent.                             |
| `tools`                 | List of tools the agent can use. Each tool entry describes the name, function, and parameters. |
| `toolFunctionFQCN`      | Fully qualified Java class name implementing the tool logic. Must be present on classpath.     |
| `requestTimeoutSeconds` | _(optional)_ How long (seconds) to wait for agent replies.                                     |

YAML makes it effortless to configure and tweak your agent’s powers and behavior—no code changes needed!

**Example agent YAML:**

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/resources/agent.yaml" language='yaml'/>

---

### Implement Tool Functions

Your agent calls out to Java classes (Tool Functions). Put these implementations on your classpath, register them in YAML.

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/tools/toolfunctions/HotelBookingLookupToolFunction.java"/>

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/tools/toolfunctions/HotelBookingToolFunction.java"/>

---

### Instantiating and Running Agents

Once your agent is described in YAML, bringing it to life in Java takes only a couple of lines:

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/AgentExample.java"/>

The API takes care of wiring up LLMs, tool invocation, and instruction handling.

Here's a sample interaction:

<TypewriterTextarea
textContent='[You]: Book a hotel in Mysuru for two guests, from July 20 to July 22.
Alright, I have booked the hotel! Room number 10 booked for 2 guests in Mysuru from July 20th to July 22nd. Here is your booking ID: HB-123'
typingSpeed={30}
pauseBetweenSentences={1200}
height='110px'
width='100%'
/>

Here's another one:

<TypewriterTextarea
textContent='[You]: Give me details of booking ID - HB-123.
I found a booking for HB-123. Looks like the hotel is booked for 2 guests. Enjoy your stay!'
typingSpeed={30}
pauseBetweenSentences={1200}
height='90px'
width='100%'
/>
