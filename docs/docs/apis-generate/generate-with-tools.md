---
sidebar_position: 2
---

# Generate - With Tools

This API lets you perform [function calling](https://docs.mistral.ai/capabilities/function_calling/) using LLMs in a
synchronous way.
This API correlates to
the [generate](https://github.com/ollama/ollama/blob/main/docs/api.md#request-raw-mode) API with `raw` mode.

:::note

This is an only an experimental implementation and has a very basic design.

Currently, built and tested for [Mistral's latest model](https://ollama.com/library/mistral) only. We could redesign
this
in the future if tooling is supported for more models with a generic interaction standard from Ollama.

:::

### Function Calling/Tools

Assume you want to call a method in your code based on the response generated from the model.
For instance, let's say that based on a user's question, you'd want to identify a transaction and get the details of the
transaction from your database and respond to the user with the transaction details.

You could do that with ease with the `function calling` capabilities of the models by registering your `tools`.

### Create Functions

This function takes the arguments `location` and `fuelType` and performs an operation with these arguments and returns a
value.

```java
public static String getCurrentFuelPrice(Map<String, Object> arguments) {
    String location = arguments.get("location").toString();
    String fuelType = arguments.get("fuelType").toString();
    return "Current price of " + fuelType + " in " + location + " is Rs.103/L";
}
```

This function takes the argument `city` and performs an operation with the argument and returns a
value.

```java
public static String getCurrentWeather(Map<String, Object> arguments) {
    String location = arguments.get("city").toString();
    return "Currently " + location + "'s weather is nice.";
}
```

### Define Tool Specifications

Lets define a sample tool specification called **Fuel Price Tool** for getting the current fuel price.

- Specify the function `name`, `description`, and `required` properties (`location` and `fuelType`).
- Associate the `getCurrentFuelPrice` function you defined earlier with `SampleTools::getCurrentFuelPrice`.

```java
MistralTools.ToolSpecification fuelPriceToolSpecification = MistralTools.ToolSpecification.builder()
        .functionName("current-fuel-price")
        .functionDesc("Get current fuel price")
        .props(
                new MistralTools.PropsBuilder()
                        .withProperty("location", MistralTools.PromptFuncDefinition.Property.builder().type("string").description("The city, e.g. New Delhi, India").required(true).build())
                        .withProperty("fuelType", MistralTools.PromptFuncDefinition.Property.builder().type("string").description("The fuel type.").enumValues(Arrays.asList("petrol", "diesel")).required(true).build())
                        .build()
        )
        .toolDefinition(SampleTools::getCurrentFuelPrice)
        .build();
```

Lets also define a sample tool specification called **Weather Tool** for getting the current weather.

- Specify the function `name`, `description`, and `required` property (`city`).
- Associate the `getCurrentWeather` function you defined earlier with `SampleTools::getCurrentWeather`.

```java
MistralTools.ToolSpecification weatherToolSpecification = MistralTools.ToolSpecification.builder()
        .functionName("current-weather")
        .functionDesc("Get current weather")
        .props(
                new MistralTools.PropsBuilder()
                        .withProperty("city", MistralTools.PromptFuncDefinition.Property.builder().type("string").description("The city, e.g. New Delhi, India").required(true).build())
                        .build()
        )
        .toolDefinition(SampleTools::getCurrentWeather)
        .build();
```

### Register the Tools

Register the defined tools (`fuel price` and `weather`) with the OllamaAPI.

```shell
ollamaAPI.registerTool(fuelPriceToolSpecification);
ollamaAPI.registerTool(weatherToolSpecification);
```

### Create prompt with Tools

`Prompt 1`: Create a prompt asking for the petrol price in Bengaluru using the defined fuel price and weather tools.

```shell
String prompt1 = new MistralTools.PromptBuilder()
        .withToolSpecification(fuelPriceToolSpecification)
        .withToolSpecification(weatherToolSpecification)
        .withPrompt("What is the petrol price in Bengaluru?")
        .build();
OllamaToolsResult toolsResult = ollamaAPI.generateWithTools(model, prompt1, false, new OptionsBuilder().build());
for (Map.Entry<ToolDef, Object> r : toolsResult.getToolResults().entrySet()) {
  System.out.printf("[Response from tool '%s']: %s%n", r.getKey().getName(), r.getValue().toString());
}
```

Now, fire away your question to the model.

You will get a response similar to:

::::tip[LLM Response]

[Response from tool 'current-fuel-price']: Current price of petrol in Bengaluru is Rs.103/L

::::

`Prompt 2`: Create a prompt asking for the current weather in Bengaluru using the same tools.

```shell
String prompt2 = new MistralTools.PromptBuilder()
        .withToolSpecification(fuelPriceToolSpecification)
        .withToolSpecification(weatherToolSpecification)
        .withPrompt("What is the current weather in Bengaluru?")
        .build();
OllamaToolsResult toolsResult = ollamaAPI.generateWithTools(model, prompt2, false, new OptionsBuilder().build());
for (Map.Entry<ToolDef, Object> r : toolsResult.getToolResults().entrySet()) {
  System.out.printf("[Response from tool '%s']: %s%n", r.getKey().getName(), r.getValue().toString());
}
```

Again, fire away your question to the model.

You will get a response similar to:

::::tip[LLM Response]

[Response from tool 'current-weather']: Currently Bengaluru's weather is nice
::::

### Full Example

```java

import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.tools.ToolDef;
import io.github.amithkoujalgi.ollama4j.core.tools.MistralTools;
import io.github.amithkoujalgi.ollama4j.core.tools.OllamaToolsResult;
import io.github.amithkoujalgi.ollama4j.core.utils.OptionsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class FunctionCallingWithMistral {
    public static void main(String[] args) throws Exception {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);
        ollamaAPI.setRequestTimeoutSeconds(60);

        String model = "mistral";


        MistralTools.ToolSpecification fuelPriceToolSpecification = MistralTools.ToolSpecification.builder()
                .functionName("current-fuel-price")
                .functionDesc("Get current fuel price")
                .props(
                        new MistralTools.PropsBuilder()
                                .withProperty("location", MistralTools.PromptFuncDefinition.Property.builder().type("string").description("The city, e.g. New Delhi, India").required(true).build())
                                .withProperty("fuelType", MistralTools.PromptFuncDefinition.Property.builder().type("string").description("The fuel type.").enumValues(Arrays.asList("petrol", "diesel")).required(true).build())
                                .build()
                )
                .toolDefinition(SampleTools::getCurrentFuelPrice)
                .build();

        MistralTools.ToolSpecification weatherToolSpecification = MistralTools.ToolSpecification.builder()
                .functionName("current-weather")
                .functionDesc("Get current weather")
                .props(
                        new MistralTools.PropsBuilder()
                                .withProperty("city", MistralTools.PromptFuncDefinition.Property.builder().type("string").description("The city, e.g. New Delhi, India").required(true).build())
                                .build()
                )
                .toolDefinition(SampleTools::getCurrentWeather)
                .build();

        ollamaAPI.registerTool(fuelPriceToolSpecification);
        ollamaAPI.registerTool(weatherToolSpecification);

        String prompt1 = new MistralTools.PromptBuilder()
                .withToolSpecification(fuelPriceToolSpecification)
                .withToolSpecification(weatherToolSpecification)
                .withPrompt("What is the petrol price in Bengaluru?")
                .build();
        String prompt2 = new MistralTools.PromptBuilder()
                .withToolSpecification(fuelPriceToolSpecification)
                .withToolSpecification(weatherToolSpecification)
                .withPrompt("What is the current weather in Bengaluru?")
                .build();

        ask(ollamaAPI, model, prompt1);
        ask(ollamaAPI, model, prompt2);
    }

    public static void ask(OllamaAPI ollamaAPI, String model, String prompt) throws OllamaBaseException, IOException, InterruptedException {
        OllamaToolsResult toolsResult = ollamaAPI.generateWithTools(model, prompt, false, new OptionsBuilder().build());
        for (Map.Entry<ToolDef, Object> r : toolsResult.getToolResults().entrySet()) {
            System.out.printf("[Response from tool '%s']: %s%n", r.getKey().getName(), r.getValue().toString());
        }
    }
}

class SampleTools {
    public static String getCurrentFuelPrice(Map<String, Object> arguments) {
        String location = arguments.get("location").toString();
        String fuelType = arguments.get("fuelType").toString();
        return "Current price of " + fuelType + " in " + location + " is Rs.103/L";
    }

    public static String getCurrentWeather(Map<String, Object> arguments) {
        String location = arguments.get("city").toString();
        return "Currently " + location + "'s weather is nice.";
    }
}

```

Run this full example and you will get a response similar to:

::::tip[LLM Response]

[Response from tool 'current-fuel-price']: Current price of petrol in Bengaluru is Rs.103/L

[Response from tool 'current-weather']: Currently Bengaluru's weather is nice
::::

### Room for improvement

Instead of explicitly registering `ollamaAPI.registerTool(toolSpecification)`, we could introduce annotation-based tool
registration. For example:

```java

@ToolSpec(name = "current-fuel-price", desc = "Get current fuel price")
public String getCurrentFuelPrice(Map<String, Object> arguments) {
    String location = arguments.get("location").toString();
    String fuelType = arguments.get("fuelType").toString();
    return "Current price of " + fuelType + " in " + location + " is Rs.103/L";
}
```

Instead of passing a map of args `Map<String, Object> arguments` to the tool functions, we could support passing
specific args separately with their data types. For example:

```shell
public String getCurrentFuelPrice(String location, String fuelType) {
    return "Current price of " + fuelType + " in " + location + " is Rs.103/L";
}
```

Updating async/chat APIs with support for tool-based generation. 