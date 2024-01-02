---
sidebar_position: 1
---

# Options Builder

This lets you build options for the `ask()` API.
Check out the supported
options [here](https://github.com/jmorganca/ollama/blob/main/docs/modelfile.md#valid-parameters-and-values).

## Build an empty Options object

```java
import io.github.amithkoujalgi.ollama4j.core.utils.Options;
import io.github.amithkoujalgi.ollama4j.core.utils.OptionsBuilder;

public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        Options options = new OptionsBuilder().build();
    }
}
```

## Build an empty Options object

```java
import io.github.amithkoujalgi.ollama4j.core.utils.Options;
import io.github.amithkoujalgi.ollama4j.core.utils.OptionsBuilder;

public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        Options options =
                new OptionsBuilder()
                        .setMirostat(10)
                        .setMirostatEta(0.5f)
                        .setNumGpu(2)
                        .setTemperature(1.5f)
                        .build();
    }
}
```