---
sidebar_position: 4

title: Agents
---

import CodeEmbed from '@site/src/components/CodeEmbed';

# Agents

Build powerful, flexible agents—backed by LLMs and tools—in a few minutes.

Ollama4j’s agent system lets you bring together the best of LLM reasoning and external tool-use using a simple, declarative YAML configuration. No framework bloat, no complicated setup—just describe your agent, plug in your logic, and go.

---

**Why use agents in Ollama4j?**

- **Effortless Customization:** Instantly adjust your agent’s persona, reasoning strategies, or domain by tweaking YAML. No need to touch your compiled Java code.
- **Easy Extensibility:** Want new capabilities? Just add or change tools and logic classes—no framework glue or plumbing required.
- **Fast Experimentation:** Mix-and-match models, instructions, and tools—prototype sophisticated behaviors or orchestrators in minutes.
- **Clean Separation:** Keep business logic (Java) and agent personality/configuration (YAML) separate for maintainability and clarity.

---

## Define an Agent in YAML

Specify everything about your agent—what LLM it uses, its “personality,” and all callable tools—in a single YAML file.

**Agent YAML keys:**

| Field                   | Description                                                                                                           |
|-------------------------|-----------------------------------------------------------------------------------------------------------------------|
| `name`                  | Name of your agent.                                                                                                   |
| `host`                  | The base URL for your Ollama server (e.g., `http://localhost:11434`).                                                |
| `model`                 | The LLM backing your agent (e.g., `llama2`, `mistral`, `mixtral`, etc).                                              |
| `customPrompt`          | _(optional)_ System prompt—instructions or persona for your agent.                                                   |
| `tools`                 | List of tools the agent can use. Each tool entry describes the name, function, and parameters.                        |
| `toolFunctionFQCN`      | Fully qualified Java class name implementing the tool logic. Must be present on classpath.                            |
| `requestTimeoutSeconds` | _(optional)_ How long (seconds) to wait for agent replies.                                                            |

YAML makes it effortless to configure and tweak your agent’s powers and behavior—no code changes needed!

**Example agent YAML:**

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/resources/agent.yaml" language='yaml'/>

---

## Implement Tool Functions

Your agent calls out to Java classes (Tool Functions). Put these implementations on your classpath, register them in YAML.

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/tools/toolfunctions/CalculatorToolFunction.java"/>

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/tools/toolfunctions/HotelBookingToolFunction.java"/>

---

## Instantiating and Running Agents

Once your agent is described in YAML, bringing it to life in Java takes only a couple of lines:

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/AgentExample.java"/>

- **No boilerplate.** Just load and start chatting or calling tools.
- The API takes care of wiring up LLMs, tool invocation, and instruction handling.

Ready to build your own AI-powered assistant? Just write your YAML, implement the tool logic in Java, and go!

