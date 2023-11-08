### Ollama4j

<img src='https://raw.githubusercontent.com/amithkoujalgi/ollama4j/163e88bc82b4beb4a52e4d99f9b5d9ef1255ec06/ollama4j.png' width='100' alt="ollama4j-icon">

A Java library (wrapper) for [Ollama](https://github.com/jmorganca/ollama/blob/main/docs/api.md) APIs.

![Build Status](https://github.com/amithkoujalgi/ollama4j/actions/workflows/maven-publish.yml/badge.svg)

#### Requirements

- Ollama (Either [natively](https://ollama.ai/download) setup or via [Docker](https://hub.docker.com/r/ollama/ollama))
- Java 8 or above

#### Install

In your Maven project, add this dependency available in
the [Central Repository](https://s01.oss.sonatype.org/#nexus-search;quick~ollama4j):

```xml

<dependency>
    <groupId>io.github.amithkoujalgi</groupId>
    <artifactId>ollama4j</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

You might want to include the Maven repository to pull the ollama4j library from. Include this in your `pom.xml`:

```xml

<repositories>
    <repository>
        <id>ollama4j-from-ossrh</id>
        <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
    </repository>
</repositories>
```

#### Build:

Build your project to resolve the dependencies:

```bash
mvn clean install
```

You can then use the Ollama Java APIs by importing `ollama4j`:

```java
import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
```

### Try out the APIs

For simplest way to get started, I prefer to use the Ollama docker setup.

Start the Ollama docker container:

```
docker run -v ~/ollama:/root/.ollama -p 11434:11434 ollama/ollama
```

#### Pull a model:

```java
public class Main {
    public static void main(String[] args) {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);
        ollamaAPI.pullModel(OllamaModel.LLAMA2);
    }
}
```

_Find the list of available models from Ollama [here](https://ollama.ai/library)._

#### Ask a question to the model with ollama4j

##### Using sync API:

```java
public class Main {
    public static void main(String[] args) {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);
        String response = ollamaAPI.runSync(OllamaModel.LLAMA2, "Who are you?");
        System.out.println(response);
    }
}
```

##### Using async API:

```java
public class Main {
    public static void main(String[] args) {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);
        OllamaAsyncResultCallback ollamaAsyncResultCallback = ollamaAPI.runAsync(OllamaModel.LLAMA2, "Who are you?");
        while (true) {
            if (ollamaAsyncResultCallback.isComplete()) {
                System.out.println(ollamaAsyncResultCallback.getResponse());
                break;
            }
            // introduce sleep to check for status with a time interval
            // Thread.sleep(1000);
        }
    }
}
```

You'd then get a response from the model:

> I am LLaMA, an AI assistant developed by Meta AI that can understand and respond to human input in a conversational
> manner. I am trained on a massive dataset of text from the internet and can generate human-like responses to a wide
> range of topics and questions. I can be used to create chatbots, virtual assistants, and other applications that
> require
> natural language understanding and generation capabilities.

#### Try asking a question from general topics:

```java
public class Main {
    public static void main(String[] args) throws Exception {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);

        String prompt = SamplePrompts.getSampleDatabasePromptWithQuestion("Give me a list of world cup cricket teams.");
        String response = ollamaAPI.ask(OllamaModelType.LLAMA2, prompt);
        System.out.println(response);
    }
}
```

You'd then get a response from the model:

> Certainly! Here are the 10 teams that have qualified for the ICC World Cup in cricket:
>
> 1. Australia
> 2. Bangladesh
> 3. England and Wales
> 4. India
> 5. Ireland
> 6. New Zealand
> 7. Pakistan
> 8. South Africa
> 9. Sri Lanka
> 10. West Indies
>
> These teams will compete in the ICC World Cup 2019, which will be held in England and Wales from May 30 to July 14,
2019.

#### Try asking for a Database query for your data schema:

```java
public class Main {
    public static void main(String[] args) throws Exception {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);

        String prompt = SamplePrompts.getSampleDatabasePromptWithQuestion("List all customer names who have bought one or more products");
        String response = ollamaAPI.ask(OllamaModelType.SQLCODER, prompt);
        System.out.println(response);
    }
}
```

_Note: Here I've used
a [sample prompt](https://github.com/amithkoujalgi/ollama4j/blob/main/src/main/resources/sample-db-prompt-template.txt)
containing a database schema from within this library for demonstration purposes._

You'd then get a response from the model:

```sql
SELECT customers.name
FROM sales
         JOIN customers ON sales.customer_id = customers.customer_id
GROUP BY customers.name;
```

Find the full `Javadoc` (API specifications) [here](https://amithkoujalgi.github.io/ollama4j/).

#### Get Involved

Contributions are most welcome! Whether it's reporting a bug, proposing an enhancement, or helping with code - any sort
of contribution is much appreciated.