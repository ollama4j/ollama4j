---
sidebar_position: 7
---

# Chat

This API lets you create a conversation with LLMs. Using this API enables you to ask questions to the model including 
information using the history of already asked questions and the respective answers.

## Create a new conversation and use chat history to augment follow up questions

```java
public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(OllamaModelType.LLAMA2);

        // create first user question
        OllamaChatRequestModel requestModel = builder.withMessage(OllamaChatMessageRole.USER,"What is the capital of France?")
             .build();

        // start conversation with model
        OllamaChatResult chatResult = ollamaAPI.chat(requestModel);

        System.out.println("First answer: " + chatResult.getResponse());

        // create next userQuestion
        requestModel = builder.withMessages(chatResult.getChatHistory()).withMessage(OllamaChatMessageRole.USER,"And what is the second largest city?").build();

        // "continue" conversation with model
        chatResult = ollamaAPI.chat(requestModel);

        System.out.println("Second answer: " + chatResult.getResponse());

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
[ {
    "role" : "user",
    "content" : "What is the capital of France?",
    "images" : [ ]
  }, {
    "role" : "assistant",
    "content" : "Should be Paris!",
    "images" : [ ]
  }, {
    "role" : "user",
    "content" : "And what is the second largest city?",
    "images" : [ ]
  }, {
    "role" : "assistant",
    "content" : "Marseille.",
    "images" : [ ]
  } ]
```

## Create a new conversation with individual system prompt
```java
public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(OllamaModelType.LLAMA2);

        // create request with system-prompt (overriding the model defaults) and user question
        OllamaChatRequestModel requestModel = builder.withMessage(OllamaChatMessageRole.SYSTEM, "You are a silent bot that only says 'NI'. Do not say anything else under any circumstances!")
             .withMessage(OllamaChatMessageRole.USER,"What is the capital of France? And what's France's connection with Mona Lisa?")
             .build();

        // start conversation with model
        OllamaChatResult chatResult = ollamaAPI.chat(requestModel);

        System.out.println(chatResult.getResponse());
    }
}

```
You will get a response similar to:

> NI.