---
sidebar_position: 1
---

import CodeEmbed from '@site/src/components/CodeEmbed';

# Generate - Sync

This API lets you ask questions to the LLMs in a synchronous way.
This API corresponds to
the [completion](https://github.com/jmorganca/ollama/blob/main/docs/api.md#generate-a-completion) API.

Use the `OptionBuilder` to build the `Options` object
with [extra parameters](https://github.com/jmorganca/ollama/blob/main/docs/modelfile.md#valid-parameters-and-values).
Refer
to [this](/apis-extras/options-builder).

## Try asking a question about the model

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/Generate.java" />

You will get a response similar to:

> I am a large language model created by Alibaba Cloud. My purpose is to assist users in generating text, answering
> questions, and completing tasks. I aim to be user-friendly and easy to understand for everyone who interacts with me.

## Try asking a question, receiving the answer streamed

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/GenerateStreamingWithTokenConcatenation.java" />

You will get a response similar to:

> The
>
> The capital
>
> The capital of
>
> The capital of France
>
> The capital of France is
>
> The capital of France is Paris
>
> The capital of France is Paris.

## Try asking a question from general topics

```java
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.types.OllamaModelType;
import io.github.ollama4j.utils.OptionsBuilder;

public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        String prompt = "List all cricket world cup teams of 2019.";

        OllamaResult result =
                ollamaAPI.generate(OllamaModelType.LLAMA2, prompt, new OptionsBuilder().build());

        System.out.println(result.getResponse());
    }
}

```

You'd then get a response from the model:

> The 2019 ICC Cricket World Cup was held in England and Wales from May 30 to July 14, 2019. The
> following teams
> participated in the tournament:
>
> 1. Afghanistan
> 2. Australia
> 3. Bangladesh
> 4. England
> 5. India
> 6. New Zealand
> 7. Pakistan
> 8. South Africa
> 9. Sri Lanka
> 10. West Indies
>
> These teams competed in a round-robin format, with the top four teams advancing to the
> semi-finals. The tournament was
> won by the England cricket team, who defeated New Zealand in the final.

## Try asking for a Database query for your data schema

```java
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.types.OllamaModelType;
import io.github.ollama4j.utils.OptionsBuilder;
import io.github.ollama4j.utils.SamplePrompts;

public class Main {

    public static void main(String[] args) {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);

        String prompt =
                SamplePrompts.getSampleDatabasePromptWithQuestion(
                        "List all customer names who have bought one or more products");
        OllamaResult result =
                ollamaAPI.generate(OllamaModelType.SQLCODER, prompt, new OptionsBuilder().build());
        System.out.println(result.getResponse());
    }
}

```

_Note: Here I've used
a [sample prompt](https://github.com/ollama4j/ollama4j/blob/main/src/main/resources/sample-db-prompt-template.txt)
containing a database schema from within this library for demonstration purposes._

You'd then get a response from the model:

```sql
SELECT customers.name
FROM sales
         JOIN customers ON sales.customer_id = customers.customer_id
GROUP BY customers.name;
```

## Generate structured output

### With response as a `Map`

```java
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.utils.Utilities;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.types.OllamaModelType;

public class StructuredOutput {

    public static void main(String[] args) throws Exception {
        String host = "http://localhost:11434/";

        OllamaAPI api = new OllamaAPI(host);

        String chatModel = "qwen2.5:0.5b";
        api.pullModel(chatModel);

        String prompt = "Ollama is 22 years old and is busy saving the world. Respond using JSON";
        Map<String, Object> format = new HashMap<>();
        format.put("type", "object");
        format.put("properties", new HashMap<String, Object>() {
            {
                put("age", new HashMap<String, Object>() {
                    {
                        put("type", "integer");
                    }
                });
                put("available", new HashMap<String, Object>() {
                    {
                        put("type", "boolean");
                    }
                });
            }
        });
        format.put("required", Arrays.asList("age", "available"));

        OllamaResult result = api.generate(chatModel, prompt, format);
        System.out.println(result);
    }
}
```

### With response mapped to specified class type

```java
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.utils.Utilities;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.types.OllamaModelType;

public class StructuredOutput {

    public static void main(String[] args) throws Exception {
        String host = Utilities.getFromConfig("host");

        OllamaAPI api = new OllamaAPI(host);

        int age = 28;
        boolean available = false;

        String prompt = "Batman is " + age + " years old and is " + (available ? "available" : "not available")
                + " because he is busy saving Gotham City. Respond using JSON";

        Map<String, Object> format = new HashMap<>();
        format.put("type", "object");
        format.put("properties", new HashMap<String, Object>() {
            {
                put("age", new HashMap<String, Object>() {
                    {
                        put("type", "integer");
                    }
                });
                put("available", new HashMap<String, Object>() {
                    {
                        put("type", "boolean");
                    }
                });
            }
        });
        format.put("required", Arrays.asList("age", "available"));

        OllamaResult result = api.generate(CHAT_MODEL_QWEN_SMALL, prompt, format);

        Person person = result.as(Person.class);
        System.out.println(person.getAge());
        System.out.println(person.getAvailable());
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Person {
    private int age;
    private boolean available;
}
```