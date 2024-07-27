---
sidebar_position: 2
---

# Generate - Async

This API lets you ask questions to the LLMs in a asynchronous way.
This is particularly helpful when you want to issue a generate request to the LLM and collect the response in the
background (such as threads) without blocking your code until the response arrives from the model.

This API corresponds to
the [completion](https://github.com/jmorganca/ollama/blob/main/docs/api.md#generate-a-completion) API.

```java
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.response.OllamaAsyncResultStreamer;
import io.github.ollama4j.types.OllamaModelType;

public class Main {

    public static void main(String[] args) throws Exception {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);
        ollamaAPI.setRequestTimeoutSeconds(60);
        String prompt = "List all cricket world cup teams of 2019.";
        OllamaAsyncResultStreamer streamer = ollamaAPI.generateAsync(OllamaModelType.LLAMA3, prompt, false);

        // Set the poll interval according to your needs. 
        // Smaller the poll interval, more frequently you receive the tokens.
        int pollIntervalMilliseconds = 1000;

        while (true) {
            String tokens = streamer.getStream().poll();
            System.out.print(tokens);
            if (!streamer.isAlive()) {
                break;
            }
            Thread.sleep(pollIntervalMilliseconds);
        }

        System.out.println("\n------------------------");
        System.out.println("Complete Response:");
        System.out.println("------------------------");

        System.out.println(streamer.getCompleteResponse());
    }
}
```