---
sidebar_position: 3
---

import CodeEmbed from '@site/src/components/CodeEmbed';

# Pull Model

This API lets you pull a model on the Ollama server.

<CodeEmbed
src='https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/PullModel.java'>
</CodeEmbed>

Once downloaded, you can see them when you use [list models](./list-models) API.

## Monitoring Progress

You can monitor the progress of a model pull by providing a `ModelPullListener`. This is useful for tracking download progress or triggering actions when the pull is complete.

### Using a Global Listener

You can set a global listener on the `Ollama` instance that will be notified of all pull and create operations.

```java
ollama.setModelPullListener((model, resp) -> {
    System.out.println("Model: " + model + " Status: " + resp.getStatus());
    if ("success".equalsIgnoreCase(resp.getStatus())) {
        System.out.println("Download complete!");
    }
});
```

### Using a Local Listener

Alternatively, you can provide a listener directly to the `pullModel` method.

```java
ollama.pullModel("llama3", (model, resp) -> {
    System.out.println("Status of " + model + ": " + resp.getStatus());
});
```
