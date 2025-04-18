---
sidebar_position: 8
---

import CodeEmbed from '@site/src/components/CodeEmbed';

# Chat with Tools

### Using Tools in Chat

If you want to have a natural back-and-forth chat experience with tools, you can directly integrate tools into
the `chat()` method, instead of using the `generateWithTools()` method. This allows you to register tools that are
automatically used during the conversation between the user and the assistant, creating a more conversational
experience.

When the model determines that a tool should be used, the tool is automatically executed. The result is then seamlessly
incorporated back into the conversation, enhancing the interaction with real-world data and actions.

The following example demonstrates usage of a simple tool, registered with the `OllamaAPI`, and then used within a chat
session. The tool invocation and response handling are all managed internally by the API.

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/ChatWithTools.java"/>

::::tip[LLM Response]
> First answer: 6527fb60-9663-4073-b59e-855526e0a0c2 is the ID of the employee named 'Rahul Kumar'.
>
> Second answer:  Kumar is the last name of the employee named 'Rahul Kumar'.
::::

This tool calling can also be done using the streaming API.

### Annotation-Based Tool Registration

Ollama4j provides a declarative and convenient way to define and register tools using Java annotations and reflection.
This approach offers an alternative to the more verbose, explicit tool registration method.

To use a method as a tool within a chat call, follow these steps:

* **Annotate the Tool Method:**
    * Use the `@ToolSpec` annotation to mark a method as a tool. This annotation describes the tool's purpose.
    * Use the `@ToolProperty` annotation to define the input parameters of the tool. The following data types are
      currently supported:
        * `java.lang.String`
        * `java.lang.Integer`
        * `java.lang.Boolean`
        * `java.math.BigDecimal`
* **Annotate the Ollama Service Class:**
    * Annotate the class that interacts with the `OllamaAPI` client using the `@OllamaToolService` annotation. Reference
      the provider class(es) containing the `@ToolSpec` annotated methods within this annotation.
* **Register the Annotated Tools:**
    * Before making a chat request with the `OllamaAPI`, call the `OllamaAPI.registerAnnotatedTools()` method. This
      registers the annotated tools, making them available for use during the chat session.

Let's try an example. Consider an `OllamaToolService` class that needs to ask the LLM a question that can only be answered by a specific tool.
This tool is implemented within a `GlobalConstantGenerator` class. Following is the code that exposes an annotated method as a tool:

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/toolcalling/annotated/GlobalConstantGenerator.java"/>

The annotated method can then be used as a tool in the chat session:

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/toolcalling/annotated/AnnotatedToolCallingExample.java"/>

Running the above would produce a response similar to:

::::tip[LLM Response]
> First answer: 0.0000112061 is the most important constant in the world using 10 digits, according to my function. This constant is known as Planck's constant and plays a fundamental role in quantum mechanics. It relates energy and frequency in electromagnetic radiation and action (the product of momentum and distance) for particles.
>
> Second answer: 3-digit constant: 8.001
::::
