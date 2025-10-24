---
sidebar_position: 5
---

import CodeEmbed from '@site/src/components/CodeEmbed';

# Generate with Tools

This API lets you perform [tool/function calling](https://docs.mistral.ai/capabilities/function_calling/) using LLMs in a
synchronous way.
This API corresponds to
the [generate](https://github.com/ollama/ollama/blob/main/docs/api.md#request-raw-mode) API with `raw` mode.

:::note

This is an only an experimental implementation and has a very basic design.

Currently, built and tested for [Mistral's latest model](https://ollama.com/library/mistral) only. We could redesign
this
in the future if tooling is supported for more models with a generic interaction standard from Ollama.

:::

## Tools/Function Calling

Assume you want to call a method/function in your code based on the response generated from the model.
For instance, let's say that based on a user's question, you'd want to identify a transaction and get the details of the
transaction from your database and respond to the user with the transaction details.

You could do that with ease with the `function calling` capabilities of the models by registering your `tools`.

### Create Tools/Functions

There are two ways to create and register your tools:

1. **Define static or regular methods and register them explicitly as tools.**
   You can create standalone functions (static or instance methods) and manually associate them with your tool specifications.

2. **Use annotation-based tool discovery for automatic registration.**
   By annotating your tool methods, you can leverage `registerAnnotatedTools()` to automatically scan your classpath, find all annotated tool functions, and register them without extra boilerplate.

Learn more about annotation-based tool registration [here](/apis-generate/chat-with-tools#annotation-based-tool-registration).

Choose the approach that best fits your project—manual for precise control, or annotation-based for easier scaling.

Let's start by exploring the first approach: manually defining and registering your tools/functions.

This function takes the arguments `location` and `fuelType` and performs an operation with these arguments and returns
fuel price value.

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/tools/toolfunctions/FuelPriceToolFunction.java"/ >

This function takes the argument `city` and performs an operation with the argument and returns the weather for a
location.

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/tools/toolfunctions/WeatherToolFunction.java"/ >

Another way to create our tools is by creating classes by extending `ToolFunction`.

This function takes the argument `employee-name` and performs an operation with the argument and returns employee
details.

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/tools/toolfunctions/EmployeeFinderToolFunction.java"/ >

### Define Tool Specifications

Lets define a sample tool specification called **Fuel Price Tool** for getting the current fuel price.

- Specify the function `name`, `description`, and `required` properties (`location` and `fuelType`).
- Associate the `getCurrentFuelPrice` function you defined earlier.

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/tools/toolspecs/FuelPriceToolSpec.java"/ >

Lets also define a sample tool specification called **Weather Tool** for getting the current weather.

- Specify the function `name`, `description`, and `required` property (`city`).
- Associate the `getCurrentWeather` function you defined earlier.

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/tools/toolspecs/WeatherToolSpec.java"/ >

Lets also define a sample tool specification called **DBQueryFunction** for getting the employee details from database.

- Specify the function `name`, `description`, and `required` property (`employee-name`).
- Associate the ToolFunction `DBQueryFunction` function you defined earlier with `new DBQueryFunction()`.

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/tools/toolspecs/EmployeeFinderToolSpec.java"/ >

Now put it all together by registering the tools and prompting with tools.

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/MultiToolRegistryExample.java"/ >

Run this full example and you will get a response similar to:

:::tip[LLM Response]

[Result of executing tool 'current-fuel-price']: Current price of petrol in Bengaluru is Rs.103/L

[Result of executing tool 'current-weather']: Currently Bengaluru's weather is nice.

[Result of executing tool 'get-employee-details']: Employee Details `{ID: 6bad82e6-b1a1-458f-a139-e3b646e092b1, Name:
Rahul Kumar, Address: King St, Hyderabad, India, Phone: 9876543210}`

:::
