---
sidebar_position: 1
---

# Intro

Let's get started with **Ollama4j**.

## Getting Started

### What you'll need

- **[Ollama](https://ollama.ai/download)**
- **[Oracle JDK](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)** or
  **[Open JDK](https://jdk.java.net/archive/)** 11.0 or above.
- **[Maven](https://maven.apache.org/download.cgi)**

### Start Ollama server

The easiest way of getting started with Ollama server is with [Docker](https://docs.docker.com/get-started/overview/).
But if you choose to run the
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

Find the latest version of the library [here](https://central.sonatype.com/artifact/io.github.amithkoujalgi/ollama4j).

You might want to include an implementation of [SL4J](https://www.slf4j.org/) logger in your `pom.xml` file. For
example,

Use `slf4j-jdk14` implementation:

```xml

<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-jdk14</artifactId>
    <version>2.0.9</version> <!--Replace with appropriate version-->
</dependency>
```

or use `logback-classic` implementation:

```xml

<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.3.11</version> <!--Replace with appropriate version-->
</dependency>
```

or use other suitable implementations.

Create a new Java class in your project and add this code.

```java
public class OllamaAPITest {

    public static void main(String[] args) {
        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        ollamaAPI.setVerbose(true);

        boolean isOllamaServerReachable = ollamaAPI.ping();

        System.out.println("Is Ollama server alive: " + isOllamaServerReachable);
    }
}
```
