### Ollama4j

A Java wrapper for [Ollama](https://github.com/jmorganca/ollama/blob/main/docs/api.md) APIs.

Start Ollama Container:
```
docker run -v ~/ollama:/root/.ollama -p 11434:11434 ollama/ollama
```

Post a question to Ollama using Ollama4j:

```java
String host = "http://localhost:11434/";

OllamaAPI ollamaAPI = new OllamaAPI(host);

ollamaAPI.pullModel(OllamaModel.LLAMA2);

OllamaAsyncResultCallback ollamaAsyncResultCallback = ollamaAPI.runAsync(OllamaModel.LLAMA2, "Who are you?");
while (true) {
    if (ollamaAsyncResultCallback.isComplete()) {
        System.out.println(ollamaAsyncResultCallback.getResponse());
        break;
    }
    Thread.sleep(1000);
}
```

You'd then get a response from Ollama:
```
I am LLaMA, an AI assistant developed by Meta AI that can understand and respond to human input in a conversational manner. I am trained on a massive dataset of text from the internet and can generate human-like responses to a wide range of topics and questions. I can be used to create chatbots, virtual assistants, and other applications that require natural language understanding and generation capabilities.
```
