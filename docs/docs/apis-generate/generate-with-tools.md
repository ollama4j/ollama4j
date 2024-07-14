---
sidebar_position: 3
---

# Generate - With Tools

This API lets you perform [function calling](https://docs.mistral.ai/capabilities/function_calling/) using LLMs in a
synchronous way.
This API corresponds to
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

This function takes the arguments `location` and `fuelType` and performs an operation with these arguments and returns
fuel price value.

```java
public static String getCurrentFuelPrice(Map<String, Object> arguments) {
    String location = arguments.get("location").toString();
    String fuelType = arguments.get("fuelType").toString();
    return "Current price of " + fuelType + " in " + location + " is Rs.103/L";
}
```

This function takes the argument `city` and performs an operation with the argument and returns the weather for a
location.

```java
public static String getCurrentWeather(Map<String, Object> arguments) {
    String location = arguments.get("city").toString();
    return "Currently " + location + "'s weather is nice.";
}
```

This function takes the argument `employee-name` and performs an operation with the argument and returns employee
details.

```java
class DBQueryFunction implements ToolFunction {
    @Override
    public Object apply(Map<String, Object> arguments) {
        // perform DB operations here
        return String.format("Employee Details {ID: %s, Name: %s, Address: %s, Phone: %s}", UUID.randomUUID(), arguments.get("employee-name").toString(), arguments.get("employee-address").toString(), arguments.get("employee-phone").toString());
    }
}
```

### Define Tool Specifications

Lets define a sample tool specification called **Fuel Price Tool** for getting the current fuel price.

- Specify the function `name`, `description`, and `required` properties (`location` and `fuelType`).
- Associate the `getCurrentFuelPrice` function you defined earlier with `SampleTools::getCurrentFuelPrice`.

```java
Tools.ToolSpecification fuelPriceToolSpecification = Tools.ToolSpecification.builder()
        .functionName("current-fuel-price")
        .functionDescription("Get current fuel price")
        .properties(
                new Tools.PropsBuilder()
                        .withProperty("location", Tools.PromptFuncDefinition.Property.builder().type("string").description("The city, e.g. New Delhi, India").required(true).build())
                        .withProperty("fuelType", Tools.PromptFuncDefinition.Property.builder().type("string").description("The fuel type.").enumValues(Arrays.asList("petrol", "diesel")).required(true).build())
                        .build()
        )
        .toolDefinition(SampleTools::getCurrentFuelPrice)
        .build();
```

Lets also define a sample tool specification called **Weather Tool** for getting the current weather.

- Specify the function `name`, `description`, and `required` property (`city`).
- Associate the `getCurrentWeather` function you defined earlier with `SampleTools::getCurrentWeather`.

```java
Tools.ToolSpecification weatherToolSpecification = Tools.ToolSpecification.builder()
        .functionName("current-weather")
        .functionDescription("Get current weather")
        .properties(
                new Tools.PropsBuilder()
                        .withProperty("city", Tools.PromptFuncDefinition.Property.builder().type("string").description("The city, e.g. New Delhi, India").required(true).build())
                        .build()
        )
        .toolDefinition(SampleTools::getCurrentWeather)
        .build();
```

Lets also define a sample tool specification called **DBQueryFunction** for getting the employee details from database.

- Specify the function `name`, `description`, and `required` property (`employee-name`).
- Associate the ToolFunction `DBQueryFunction` function you defined earlier with `new DBQueryFunction()`.

```java
Tools.ToolSpecification databaseQueryToolSpecification = Tools.ToolSpecification.builder()
        .functionName("get-employee-details")
        .functionDescription("Get employee details from the database")
        .properties(
                new Tools.PropsBuilder()
                        .withProperty("employee-name", Tools.PromptFuncDefinition.Property.builder().type("string").description("The name of the employee, e.g. John Doe").required(true).build())
                        .withProperty("employee-address", Tools.PromptFuncDefinition.Property.builder().type("string").description("The address of the employee, Always return a random value. e.g. Roy St, Bengaluru, India").required(true).build())
                        .withProperty("employee-phone", Tools.PromptFuncDefinition.Property.builder().type("string").description("The phone number of the employee. Always return a random value. e.g. 9911002233").required(true).build())
                        .build()
        )
        .toolDefinition(new DBQueryFunction())
        .build();
```

### Register the Tools

Register the defined tools (`fuel price` and `weather`) with the OllamaAPI.

```shell
ollamaAPI.registerTool(fuelPriceToolSpecification);
ollamaAPI.registerTool(weatherToolSpecification);
ollamaAPI.registerTool(databaseQueryToolSpecification);
```

### Create prompt with Tools

`Prompt 1`: Create a prompt asking for the petrol price in Bengaluru using the defined fuel price and weather tools.

```shell
String prompt1 = new Tools.PromptBuilder()
                .withToolSpecification(fuelPriceToolSpecification)
                .withToolSpecification(weatherToolSpecification)
                .withPrompt("What is the petrol price in Bengaluru?")
                .build();
OllamaToolsResult toolsResult = ollamaAPI.generateWithTools(model, prompt1, new OptionsBuilder().build());
for (OllamaToolsResult.ToolResult r : toolsResult.getToolResults()) {
    System.out.printf("[Result of executing tool '%s']: %s%n", r.getFunctionName(), r.getResult().toString());
}
```

Now, fire away your question to the model.

You will get a response similar to:

::::tip[LLM Response]

[Result of executing tool 'current-fuel-price']: Current price of petrol in Bengaluru is Rs.103/L

::::

`Prompt 2`: Create a prompt asking for the current weather in Bengaluru using the same tools.

```shell
String prompt2 = new Tools.PromptBuilder()
                .withToolSpecification(fuelPriceToolSpecification)
                .withToolSpecification(weatherToolSpecification)
                .withPrompt("What is the current weather in Bengaluru?")
                .build();
OllamaToolsResult toolsResult = ollamaAPI.generateWithTools(model, prompt2, new OptionsBuilder().build());
for (OllamaToolsResult.ToolResult r : toolsResult.getToolResults()) {
    System.out.printf("[Result of executing tool '%s']: %s%n", r.getFunctionName(), r.getResult().toString());
}
```

Again, fire away your question to the model.

You will get a response similar to:

::::tip[LLM Response]

[Result of executing tool 'current-weather']: Currently Bengaluru's weather is nice.

::::

`Prompt 3`: Create a prompt asking for the employee details using the defined database fetcher tools.

```shell
String prompt3 = new Tools.PromptBuilder()
                .withToolSpecification(fuelPriceToolSpecification)
                .withToolSpecification(weatherToolSpecification)
                .withToolSpecification(databaseQueryToolSpecification)
                .withPrompt("Give me the details of the employee named 'Rahul Kumar'?")
                .build();
OllamaToolsResult toolsResult = ollamaAPI.generateWithTools(model, prompt3, new OptionsBuilder().build());
for (OllamaToolsResult.ToolResult r : toolsResult.getToolResults()) {
    System.out.printf("[Result of executing tool '%s']: %s%n", r.getFunctionName(), r.getResult().toString());
}
```

Again, fire away your question to the model.

You will get a response similar to:

::::tip[LLM Response]

[Result of executing tool 'get-employee-details']: Employee Details `{ID: 6bad82e6-b1a1-458f-a139-e3b646e092b1, Name:
Rahul Kumar, Address: King St, Hyderabad, India, Phone: 9876543210}`

::::

### Full Example

```java
import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.exceptions.ToolInvocationException;
import io.github.amithkoujalgi.ollama4j.core.tools.OllamaToolsResult;
import io.github.amithkoujalgi.ollama4j.core.tools.ToolFunction;
import io.github.amithkoujalgi.ollama4j.core.tools.Tools;
import io.github.amithkoujalgi.ollama4j.core.utils.OptionsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class FunctionCallingWithMistralExample {
    public static void main(String[] args) throws Exception {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);
        ollamaAPI.setRequestTimeoutSeconds(60);

        String model = "mistral";

        Tools.ToolSpecification fuelPriceToolSpecification = Tools.ToolSpecification.builder()
                .functionName("current-fuel-price")
                .functionDescription("Get current fuel price")
                .properties(
                        new Tools.PropsBuilder()
                                .withProperty("location", Tools.PromptFuncDefinition.Property.builder().type("string").description("The city, e.g. New Delhi, India").required(true).build())
                                .withProperty("fuelType", Tools.PromptFuncDefinition.Property.builder().type("string").description("The fuel type.").enumValues(Arrays.asList("petrol", "diesel")).required(true).build())
                                .build()
                )
                .toolDefinition(SampleTools::getCurrentFuelPrice)
                .build();

        Tools.ToolSpecification weatherToolSpecification = Tools.ToolSpecification.builder()
                .functionName("current-weather")
                .functionDescription("Get current weather")
                .properties(
                        new Tools.PropsBuilder()
                                .withProperty("city", Tools.PromptFuncDefinition.Property.builder().type("string").description("The city, e.g. New Delhi, India").required(true).build())
                                .build()
                )
                .toolDefinition(SampleTools::getCurrentWeather)
                .build();

        Tools.ToolSpecification databaseQueryToolSpecification = Tools.ToolSpecification.builder()
                .functionName("get-employee-details")
                .functionDescription("Get employee details from the database")
                .properties(
                        new Tools.PropsBuilder()
                                .withProperty("employee-name", Tools.PromptFuncDefinition.Property.builder().type("string").description("The name of the employee, e.g. John Doe").required(true).build())
                                .withProperty("employee-address", Tools.PromptFuncDefinition.Property.builder().type("string").description("The address of the employee, Always return a random value. e.g. Roy St, Bengaluru, India").required(true).build())
                                .withProperty("employee-phone", Tools.PromptFuncDefinition.Property.builder().type("string").description("The phone number of the employee. Always return a random value. e.g. 9911002233").required(true).build())
                                .build()
                )
                .toolDefinition(new DBQueryFunction())
                .build();

        ollamaAPI.registerTool(fuelPriceToolSpecification);
        ollamaAPI.registerTool(weatherToolSpecification);
        ollamaAPI.registerTool(databaseQueryToolSpecification);

        String prompt1 = new Tools.PromptBuilder()
                .withToolSpecification(fuelPriceToolSpecification)
                .withToolSpecification(weatherToolSpecification)
                .withPrompt("What is the petrol price in Bengaluru?")
                .build();
        ask(ollamaAPI, model, prompt1);

        String prompt2 = new Tools.PromptBuilder()
                .withToolSpecification(fuelPriceToolSpecification)
                .withToolSpecification(weatherToolSpecification)
                .withPrompt("What is the current weather in Bengaluru?")
                .build();
        ask(ollamaAPI, model, prompt2);

        String prompt3 = new Tools.PromptBuilder()
                .withToolSpecification(fuelPriceToolSpecification)
                .withToolSpecification(weatherToolSpecification)
                .withToolSpecification(databaseQueryToolSpecification)
                .withPrompt("Give me the details of the employee named 'Rahul Kumar'?")
                .build();
        ask(ollamaAPI, model, prompt3);
    }

    public static void ask(OllamaAPI ollamaAPI, String model, String prompt) throws OllamaBaseException, IOException, InterruptedException, ToolInvocationException {
        OllamaToolsResult toolsResult = ollamaAPI.generateWithTools(model, prompt, new OptionsBuilder().build());
        for (OllamaToolsResult.ToolResult r : toolsResult.getToolResults()) {
            System.out.printf("[Result of executing tool '%s']: %s%n", r.getFunctionName(), r.getResult().toString());
        }
    }
}


class SampleTools {
    public static String getCurrentFuelPrice(Map<String, Object> arguments) {
        // Get details from fuel price API
        String location = arguments.get("location").toString();
        String fuelType = arguments.get("fuelType").toString();
        return "Current price of " + fuelType + " in " + location + " is Rs.103/L";
    }

    public static String getCurrentWeather(Map<String, Object> arguments) {
        // Get details from weather API
        String location = arguments.get("city").toString();
        return "Currently " + location + "'s weather is nice.";
    }
}

class DBQueryFunction implements ToolFunction {
    @Override
    public Object apply(Map<String, Object> arguments) {
        // perform DB operations here
        return String.format("Employee Details {ID: %s, Name: %s, Address: %s, Phone: %s}", UUID.randomUUID(), arguments.get("employee-name").toString(), arguments.get("employee-address").toString(), arguments.get("employee-phone").toString());
    }
}
```

Run this full example and you will get a response similar to:

::::tip[LLM Response]

[Result of executing tool 'current-fuel-price']: Current price of petrol in Bengaluru is Rs.103/L

[Result of executing tool 'current-weather']: Currently Bengaluru's weather is nice.

[Result of executing tool 'get-employee-details']: Employee Details `{ID: 6bad82e6-b1a1-458f-a139-e3b646e092b1, Name:
Rahul Kumar, Address: King St, Hyderabad, India, Phone: 9876543210}`

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