---
sidebar_position: 1
---

# Intro

Let's discover **Ollama4J in less than 5 minutes**.

## Getting Started

### What you'll need

- **[Ollama](https://ollama.ai/download)**
- **[Oracle JDK](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)** or
  **[Open JDK](https://jdk.java.net/archive/)** 11.0 or above.
- **[Maven](https://maven.apache.org/download.cgi)** or **[Gradle](https://gradle.org/install/)**

### Start Ollama server

The easiest way of getting started with Ollama server is with *
*[Docker](https://docs.docker.com/get-started/overview/)**. But if you choose to run the
Ollama server directly, **[download](https://ollama.ai/download)** the distribution of your choice
and follow the installation process.

#### With Docker

##### Run in CPU mode:

```bash
docker run -it -v ~/ollama:/root/.ollama -p 11434:11434 ollama/ollama
```

##### Run in GPU mode:

```bash
docker run -it --gpus=all -v ~/ollama:/root/.ollama -p 11434:11434 ollama/ollama
```

You can type this command into Command Prompt, Powershell, Terminal, or any other integrated
terminal of your code editor.

The command runs the Ollama server locally at **http://localhost:11434/**.

### Setup your project

Get started by **creating a new Maven project** on your favorite IDE.

Add the dependency to your project's `pom.xml`.

```xml

<dependency>
  <groupId>io.github.amithkoujalgi</groupId>
  <artifactId>ollama4j</artifactId>
  <version>1.0.27</version>
</dependency>
```

Create a new Java class in your project and add this code.

```java
public class OllamaAPITest {

  public static void main(String[] args) {
    String host = "http://localhost:11434/";
    
    OllamaAPI ollamaAPI = new OllamaAPI(host);

    ollamaAPI.setVerbose(true);

    boolean isOllamaServerReachable  = ollamaAPI.ping();

    System.out.println("Is Ollama server alive: " + isOllamaServerReachable);
  }
}
```
