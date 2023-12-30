---
sidebar_position: 4
---

# Ask - With Image URLs

This API lets you ask questions along with the image files to the LLMs.
These APIs correlate to
the [completion](https://github.com/jmorganca/ollama/blob/main/docs/api.md#generate-a-completion) APIs.

:::note

Executing this on Ollama server running in CPU-mode will take longer to generate response. Hence, GPU-mode is
recommended.

:::

## Ask (Sync)

Passing the link of this image the following code:

![Img](https://t3.ftcdn.net/jpg/02/96/63/80/360_F_296638053_0gUVA4WVBKceGsIr7LNqRWSnkusi07dq.jpg)

```java
public class Main {

    public static void main(String[] args) {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);
        ollamaAPI.setRequestTimeoutSeconds(10);

        OllamaResult result = ollamaAPI.askWithImageURLs(OllamaModelType.LLAVA,
                "What's in this image?",
                List.of(
                        "https://t3.ftcdn.net/jpg/02/96/63/80/360_F_296638053_0gUVA4WVBKceGsIr7LNqRWSnkusi07dq.jpg"));
        System.out.println(result.getResponse());
    }
}
```

You will get a response similar to:

> This image features a white boat with brown cushions, where a dog is sitting on the back of the boat. The dog seems to
> be enjoying its time outdoors, perhaps on a lake.