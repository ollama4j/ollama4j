### Ollama4j

<img src='https://raw.githubusercontent.com/amithkoujalgi/ollama4j/163e88bc82b4beb4a52e4d99f9b5d9ef1255ec06/ollama4j.png' width='100' alt="ollama4j-icon">

A Java wrapper for [Ollama](https://github.com/jmorganca/ollama/blob/main/docs/api.md) APIs.

![Build Status](https://github.com/amithkoujalgi/ollama4j/actions/workflows/maven-publish.yml/badge.svg)

Install:

With Maven from [Nexus](https://s01.oss.sonatype.org/#nexus-search;quick~ollama4j):

```xml
<dependency>
    <groupId>io.github.amithkoujalgi</groupId>
    <artifactId>ollama4j</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
Start Ollama Container:
```
docker run -v ~/ollama:/root/.ollama -p 11434:11434 ollama/ollama
```

Pull a model:

```java
public class Main {
    public static void main(String[] args) throws Exception {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);
        ollamaAPI.pullModel(OllamaModel.LLAMA2);
    }
}
```
Post a question to Ollama using Ollama4j:

Using sync API:

```java
public class Main {
    public static void main(String[] args) throws Exception {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);
        String response = ollamaAPI.runSync(OllamaModel.LLAMA2, "Who are you?");
        System.out.println(response);
    }
}
```

Using async API:

```java
public class Main {
    public static void main(String[] args) throws Exception {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);
        OllamaAsyncResultCallback ollamaAsyncResultCallback = ollamaAPI.runAsync(OllamaModel.LLAMA2, "Who are you?");
        while (true) {
            if (ollamaAsyncResultCallback.isComplete()) {
                System.out.println(ollamaAsyncResultCallback.getResponse());
                break;
            }
            Thread.sleep(1000);
        }
    }
}
```

You'd then get a response from Ollama:
```
I am LLaMA, an AI assistant developed by Meta AI that can understand and respond to human input in a conversational manner. I am trained on a massive dataset of text from the internet and can generate human-like responses to a wide range of topics and questions. I can be used to create chatbots, virtual assistants, and other applications that require natural language understanding and generation capabilities.
```
