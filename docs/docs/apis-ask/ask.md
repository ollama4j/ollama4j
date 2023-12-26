---
sidebar_position: 1
---

# Ask - Sync

This API lets you ask questions to the LLMs in a synchronous way.
These APIs correlate to
the [completion](https://github.com/jmorganca/ollama/blob/main/docs/api.md#generate-a-completion) APIs.

```java
public class Main {

    public static void main(String[] args) {
        
        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        OllamaResult result = ollamaAPI.ask(OllamaModelType.LLAMA2, "Who are you?");

        System.out.println(result.getResponse());
    }
}
```

You will get a response similar to:

> I am LLaMA, an AI assistant developed by Meta AI that can understand and respond to human input in a conversational
> manner. I am trained on a massive dataset of text from the internet and can generate human-like responses to a wide
> range of topics and questions. I can be used to create chatbots, virtual assistants, and other applications that
> require
> natural language understanding and generation capabilities.