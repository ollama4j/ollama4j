---
sidebar_position: 6
---

# List Models from Library

This API retrieves a list of models from the Ollama library. It fetches available models directly from the Ollama
library page, including details such as the model's name, pull count, popular tags, tag count, and the last update time.

```java title="ListLibraryModels.java"
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.response.LibraryModel;

import java.util.List;

public class ListModels {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        List<LibraryModel> libraryModels = ollamaAPI.listModelsFromLibrary();

        System.out.println(libraryModels);
    }
}
```

The following is the sample response:

```
[
    LibraryModel(name=llama3.2-vision, description=Llama 3.2 Vision is a collection of instruction-tuned image reasoning generative models in 11B and 90B sizes., pullCount=20.6K, totalTags=9, popularTags=[vision, 11b, 90b], lastUpdated=yesterday), 
    LibraryModel(name=llama3.2, description=Meta's Llama 3.2 goes small with 1B and 3B models., pullCount=2.4M, totalTags=63, popularTags=[tools, 1b, 3b], lastUpdated=6 weeks ago)
]
```