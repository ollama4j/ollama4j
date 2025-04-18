---
sidebar_position: 7
---

import CodeEmbed from '@site/src/components/CodeEmbed';

# Chat

This API lets you create a conversation with LLMs. Using this API enables you to ask questions to the model including
information using the history of already asked questions and the respective answers.



## Create a new conversation and use chat history to augment follow up questions

```java
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.types.OllamaModelType;

public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(OllamaModelType.LLAMA2);

        // create first user question
        OllamaChatRequest requestModel = builder.withMessage(OllamaChatMessageRole.USER, "What is the capital of France?")
                .build();

        // start conversation with model
        OllamaChatResult chatResult = ollamaAPI.chat(requestModel);

        System.out.println("First answer: " + chatResult.getResponseModel().getMessage().getContent());

        // create next userQuestion
        requestModel = builder.withMessages(chatResult.getChatHistory()).withMessage(OllamaChatMessageRole.USER, "And what is the second largest city?").build();

        // "continue" conversation with model
        chatResult = ollamaAPI.chat(requestModel);

        System.out.println("Second answer: " + chatResult.getResponseModel().getMessage().getContent());

        System.out.println("Chat History: " + chatResult.getChatHistory());
    }
}

```

You will get a response similar to:

> First answer: Should be Paris!
>
> Second answer: Marseille.
>
> Chat History:

```json
[
  {
    "role": "user",
    "content": "What is the capital of France?",
    "images": []
  },
  {
    "role": "assistant",
    "content": "Should be Paris!",
    "images": []
  },
  {
    "role": "user",
    "content": "And what is the second largest city?",
    "images": []
  },
  {
    "role": "assistant",
    "content": "Marseille.",
    "images": []
  }
]
```

## Conversational loop

```java
public class Main {

    public static void main(String[] args) {

        OllamaAPI ollamaAPI = new OllamaAPI();
        ollamaAPI.setRequestTimeoutSeconds(60);

        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance("<your-model>");

        OllamaChatRequest requestModel = builder.withMessage(OllamaChatMessageRole.USER, "<your-first-message>").build();
        OllamaChatResult initialChatResult = ollamaAPI.chat(requestModel);
        System.out.println(initialChatResult.getResponse());

        List<OllamaChatMessage> history = initialChatResult.getChatHistory();

        while (true) {
            OllamaChatResult chatResult = ollamaAPI.chat(builder.withMessages(history).withMessage(OllamaChatMessageRole.USER, "<your-new-message").build());
            System.out.println(chatResult.getResponse());
            history = chatResult.getChatHistory();
        }
    }
}
```

## Create a conversation where the answer is streamed

```java
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.generate.OllamaStreamHandler;


public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(config.getModel());
        OllamaChatRequest requestModel = builder.withMessage(OllamaChatMessageRole.USER,
                        "What is the capital of France? And what's France's connection with Mona Lisa?")
                .build();

        // define a handler (Consumer<String>)
        OllamaStreamHandler streamHandler = (s) -> {
            System.out.println(s);
        };

        OllamaChatResult chatResult = ollamaAPI.chat(requestModel, streamHandler);
    }
}
```

You will get a response similar to:

> The
> The capital
> The capital of
> The capital of France
> The capital of France is
> The capital of France is Paris
> The capital of France is Paris.

## Use a simple Console Output Stream Handler

```java
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.impl.ConsoleOutputStreamHandler;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.generate.OllamaStreamHandler;
import io.github.ollama4j.types.OllamaModelType;

public class Main {
    public static void main(String[] args) throws Exception {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);

        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(OllamaModelType.LLAMA2);
        OllamaChatRequest requestModel = builder.withMessage(OllamaChatMessageRole.USER, "List all cricket world cup teams of 2019. Name the teams!")
                .build();
        OllamaStreamHandler streamHandler = new ConsoleOutputStreamHandler();
        ollamaAPI.chat(requestModel, streamHandler);
    }
}
```

## Create a new conversation with individual system prompt

```java
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.types.OllamaModelType;


public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(OllamaModelType.LLAMA2);

        // create request with system-prompt (overriding the model defaults) and user question
        OllamaChatRequest requestModel = builder.withMessage(OllamaChatMessageRole.SYSTEM, "You are a silent bot that only says 'NI'. Do not say anything else under any circumstances!")
                .withMessage(OllamaChatMessageRole.USER, "What is the capital of France? And what's France's connection with Mona Lisa?")
                .build();

        // start conversation with model
        OllamaChatResult chatResult = ollamaAPI.chat(requestModel);

        System.out.println(chatResult.getResponseModel());
    }
}

```

You will get a response similar to:

> NI.

## Create a conversation about an image (requires model with image recognition skills)

```java
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.types.OllamaModelType;

import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(OllamaModelType.LLAVA);

        // Load Image from File and attach to user message (alternatively images could also be added via URL)
        OllamaChatRequest requestModel =
                builder.withMessage(OllamaChatMessageRole.USER, "What's in the picture?",
                        List.of(
                                new File("/path/to/image"))).build();

        OllamaChatResult chatResult = ollamaAPI.chat(requestModel);
        System.out.println("First answer: " + chatResult.getResponseModel());

        builder.reset();

        // Use history to ask further questions about the image or assistant answer
        requestModel =
                builder.withMessages(chatResult.getChatHistory())
                        .withMessage(OllamaChatMessageRole.USER, "What's the dogs breed?").build();

        chatResult = ollamaAPI.chat(requestModel);
        System.out.println("Second answer: " + chatResult.getResponseModel());
    }
}
```

You will get a response similar to:

> First Answer: The image shows a dog sitting on the bow of a boat that is docked in calm water. The boat has two
> levels, with the lower level containing seating and what appears to be an engine cover. The dog seems relaxed and
> comfortable on the boat, looking out over the water. The background suggests it might be late afternoon or early
> evening, given the warm lighting and the low position of the sun in the sky.
>
> Second Answer: Based on the image, it's difficult to definitively determine the breed of the dog. However, the dog
> appears to be medium-sized with a short coat and a brown coloration, which might suggest that it is a Golden Retriever
> or a similar breed. Without more details like ear shape and tail length, it's not possible to identify the exact breed
> confidently.

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/ChatExample.java" />