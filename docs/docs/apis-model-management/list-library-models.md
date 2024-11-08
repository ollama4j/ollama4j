---
sidebar_position: 6
---

# List Models from Ollama Library

This API retrieves a list of models from the Ollama library. It fetches available models directly from the Ollama
library page, including details such as the model's name, pull count, popular tags, tag count, and the last update time.

```java title="ListLibraryModels.java"
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.response.LibraryModel;

import java.util.List;

public class Main {

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
    LibraryModel(name=llama3.2-vision, description=Llama 3.2 Vision is a collection of instruction-tuned image reasoning generative models in 11B and 90B sizes., pullCount=21.1K, totalTags=9, popularTags=[vision, 11b, 90b], lastUpdated=yesterday), 
    LibraryModel(name=llama3.2, description=Meta's Llama 3.2 goes small with 1B and 3B models., pullCount=2.4M, totalTags=63, popularTags=[tools, 1b, 3b], lastUpdated=6 weeks ago)
]
```

# Get Tags of a Library Model

This API Fetches the tags associated with a specific model from Ollama library.

```java title="GetLibraryModelTags.java"
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.response.LibraryModel;
import io.github.ollama4j.models.response.LibraryModelDetail;

public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        List<LibraryModel> libraryModels = ollamaAPI.listModelsFromLibrary();

        LibraryModelDetail libraryModelDetail = ollamaAPI.getLibraryModelDetails(libraryModels.get(0));

        System.out.println(libraryModelDetail);
    }
}
```

```
LibraryModelDetail(
  model=LibraryModel(name=llama3.2-vision, description=Llama 3.2 Vision is a collection of instruction-tuned image reasoning generative models in 11B and 90B sizes., pullCount=21.1K, totalTags=9, popularTags=[vision, 11b, 90b], lastUpdated=yesterday), 
  tags=[
        LibraryModelTag(name=llama3.2-vision, tag=latest, size=7.9GB, lastUpdated=yesterday), 
        LibraryModelTag(name=llama3.2-vision, tag=11b, size=7.9GB, lastUpdated=yesterday), 
        LibraryModelTag(name=llama3.2-vision, tag=90b, size=55GB, lastUpdated=yesterday)
    ]
)
```

You can use this information to pull models into Ollama server.

```java title="PullLibraryModelTags.java"
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.response.LibraryModel;
import io.github.ollama4j.models.response.LibraryModelDetail;
import io.github.ollama4j.models.response.LibraryModelTag;

public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        List<LibraryModel> libraryModels = ollamaAPI.listModelsFromLibrary();

        LibraryModelDetail libraryModelDetail = ollamaAPI.getLibraryModelDetails(libraryModels.get(0));

        LibraryModelTag libraryModelTag = libraryModelDetail.getTags().get(0);

        ollamaAPI.pullModel(libraryModelTag);
    }
}
```