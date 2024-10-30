---
sidebar_position: 6
---

# Generate Embeddings

Generate embeddings from a model.

Parameters:

- `model`: name of model to generate embeddings from
- `input`: text/s to generate embeddings for

```java
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.types.OllamaModelType;
import io.github.ollama4j.models.embeddings.OllamaEmbedRequestModel;
import io.github.ollama4j.models.embeddings.OllamaEmbedResponseModel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        OllamaEmbedResponseModel embeddings = ollamaAPI.embed("all-minilm", Arrays.asList("Why is the sky blue?", "Why is the grass green?"));

        System.out.println(embeddings);
    }
}
```

Or, using the `OllamaEmbedRequestModel`:

```java
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.types.OllamaModelType;
import io.github.ollama4j.models.embeddings.OllamaEmbedRequestModel;
import io.github.ollama4j.models.embeddings.OllamaEmbedResponseModel;import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        OllamaEmbedResponseModel embeddings = ollamaAPI.embed(new OllamaEmbedRequestModel("all-minilm", Arrays.asList("Why is the sky blue?", "Why is the grass green?")));

        System.out.println(embeddings);
    }
}
```

You will get a response similar to:

```json
{
    "model": "all-minilm",
    "embeddings": [[-0.034674067, 0.030984823, 0.0067988685]],
    "total_duration": 14173700,
    "load_duration": 1198800,
    "prompt_eval_count": 2
}
````

:::note

This is a deprecated API

:::

Parameters:

- `model`: name of model to generate embeddings from
- `prompt`: text to generate embeddings for

```java
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.types.OllamaModelType;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        List<Double> embeddings = ollamaAPI.generateEmbeddings(OllamaModelType.LLAMA2,
                "Here is an article about llamas...");

        embeddings.forEach(System.out::println);
    }
}
```

You will get a response similar to:

```javascript
 [
    0.5670403838157654,
    0.009260174818336964,
    0.23178744316101074,
    -0.2916173040866852,
    -0.8924556970596313
]
```