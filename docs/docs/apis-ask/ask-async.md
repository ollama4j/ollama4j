---
sidebar_position: 2
---

# Ask - Async

This API lets you ask questions to the LLMs in a asynchronous way.
These APIs correlate to
the [completion](https://github.com/jmorganca/ollama/blob/main/docs/api.md#generate-a-completion) APIs.

```java
public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        OllamaAsyncResultCallback ollamaAsyncResultCallback = ollamaAPI.askAsync(OllamaModelType.LLAMA2,
                "Who are you?");

        while (true) {
            if (ollamaAsyncResultCallback.isComplete()) {
                System.out.println(ollamaAsyncResultCallback.getResponse());
                break;
            }
            // introduce sleep to check for status with a time interval
            Thread.sleep(100);
        }
    }
}
```

You will get a response similar to:

> I am LLaMA, an AI assistant developed by Meta AI that can understand and respond to human input in a conversational
> manner. I am trained on a massive dataset of text from the internet and can generate human-like responses to a wide
> range of topics and questions. I can be used to create chatbots, virtual assistants, and other applications that
> require
> natural language understanding and generation capabilities.