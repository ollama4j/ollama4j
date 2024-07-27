---
sidebar_position: 4
---

# Generate - With Image Files

This API lets you ask questions along with the image files to the LLMs.
This API corresponds to
the [completion](https://github.com/jmorganca/ollama/blob/main/docs/api.md#generate-a-completion) API.

:::note

Executing this on Ollama server running in CPU-mode will take longer to generate response. Hence, GPU-mode is
recommended.

:::

## Synchronous mode

If you have this image downloaded and you pass the path to the downloaded image to the following code:

![Img](https://t3.ftcdn.net/jpg/02/96/63/80/360_F_296638053_0gUVA4WVBKceGsIr7LNqRWSnkusi07dq.jpg)

```java
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.OllamaResult;
import io.github.ollama4j.types.OllamaModelType;
import io.github.ollama4j.utils.OptionsBuilder;
import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);
        ollamaAPI.setRequestTimeoutSeconds(10);

        OllamaResult result = ollamaAPI.generateWithImageFiles(OllamaModelType.LLAVA,
                "What's in this image?",
                List.of(
                        new File("/path/to/image")),
                new OptionsBuilder().build()
        );
        System.out.println(result.getResponse());
    }
}
```

You will get a response similar to:

> This image features a white boat with brown cushions, where a dog is sitting on the back of the boat. The dog seems to
> be enjoying its time outdoors, perhaps on a lake.