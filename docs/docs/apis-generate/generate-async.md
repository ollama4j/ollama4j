---
sidebar_position: 2
---

# Generate - Async

This API lets you ask questions to the LLMs in a asynchronous way.
These APIs correlate to
the [completion](https://github.com/jmorganca/ollama/blob/main/docs/api.md#generate-a-completion) APIs.

```java
public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        String prompt = "Who are you?";

        OllamaAsyncResultCallback callback = ollamaAPI.generateAsync(OllamaModelType.LLAMA2, prompt);

        while (!callback.isComplete() || !callback.getStream().isEmpty()) {
            // poll for data from the response stream
            String result = callback.getStream().poll();
            if (result != null) {
                System.out.print(result);
            }
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