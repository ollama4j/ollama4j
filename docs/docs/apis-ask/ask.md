---
sidebar_position: 1
---

# Ask - Sync

This API lets you ask questions to the LLMs in a synchronous way.
These APIs correlate to
the [completion](https://github.com/jmorganca/ollama/blob/main/docs/api.md#generate-a-completion) APIs.

Use the `OptionBuilder` to build the `Options` object
with [extra parameters](https://github.com/jmorganca/ollama/blob/main/docs/modelfile.md#valid-parameters-and-values).
Refer
to [this](/docs/apis-extras/options-builder).

## Try asking a question about the model.

```java
public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        OllamaResult result =
                ollamaAPI.ask(OllamaModelType.LLAMA2, "Who are you?", new OptionsBuilder().build());

        System.out.println(result.getResponse());
    }
}

```

You will get a response similar to:

> I am LLaMA, an AI assistant developed by Meta AI that can understand and respond to human input in a conversational
> manner. I am trained on a massive dataset of text from the internet and can generate human-like responses to a wide
> range of topics and questions. I can be used to create chatbots, virtual assistants, and other applications that
> require
> natural language understanding and generation capabilities.

## Try asking a question from general topics.

```java
public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        String prompt = "List all cricket world cup teams of 2019.";

        OllamaResult result =
                ollamaAPI.ask(OllamaModelType.LLAMA2, prompt, new OptionsBuilder().build());

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

## Try asking for a Database query for your data schema.

```java
public class Main {

    public static void main(String[] args) {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);

        String prompt =
                SamplePrompts.getSampleDatabasePromptWithQuestion(
                        "List all customer names who have bought one or more products");
        OllamaResult result =
                ollamaAPI.ask(OllamaModelType.SQLCODER, prompt, new OptionsBuilder().build());
        System.out.println(result.getResponse());
    }
}

```

_Note: Here I've used
a [sample prompt](https://github.com/amithkoujalgi/ollama4j/blob/main/src/main/resources/sample-db-prompt-template.txt)
containing a database schema from within this library for demonstration purposes._

You'd then get a response from the model:

```sql
SELECT customers.name
FROM sales
         JOIN customers ON sales.customer_id = customers.customer_id
GROUP BY customers.name;
```