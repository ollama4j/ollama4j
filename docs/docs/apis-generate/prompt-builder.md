---
sidebar_position: 10
---

# Prompt Builder

This is designed for prompt engineering. It allows you to easily build the prompt text for zero-shot, one-shot, few-shot
inferences.

```java
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.types.OllamaModelType;
import io.github.ollama4j.utils.OptionsBuilder;
import io.github.ollama4j.utils.PromptBuilder;

public class Main {
    public static void main(String[] args) throws Exception {

        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);
        ollamaAPI.setRequestTimeoutSeconds(10);

        String model = OllamaModelType.PHI;

        PromptBuilder promptBuilder =
                new PromptBuilder()
                        .addLine("You are an expert coder and understand different programming languages.")
                        .addLine("Given a question, answer ONLY with code.")
                        .addLine("Produce clean, formatted and indented code in markdown format.")
                        .addLine(
                                "DO NOT include ANY extra text apart from code. Follow this instruction very strictly!")
                        .addLine("If there's any additional information you want to add, use comments within code.")
                        .addLine("Answer only in the programming language that has been asked for.")
                        .addSeparator()
                        .addLine("Example: Sum 2 numbers in Python")
                        .addLine("Answer:")
                        .addLine("```python")
                        .addLine("def sum(num1: int, num2: int) -> int:")
                        .addLine("    return num1 + num2")
                        .addLine("```")
                        .addSeparator()
                        .add("How do I read a file in Go and print its contents to stdout?");

        boolean raw = false;
        OllamaResult response = ollamaAPI.generate(model, promptBuilder.build(), raw, new OptionsBuilder().build());
        System.out.println(response.getResponse());
    }
}
```

You will get a response similar to:

```go
package main

import (
    "fmt"
    "io/ioutil"
)

func readFile(fileName string) {
    file, err := ioutil.ReadFile(fileName)
    if err != nil {
        fmt.Fprintln(os.Stderr, "Error reading file:", err.Error())
        return
    }

    f, _ := ioutil.ReadFile("file.txt")
    if f != nil {
        fmt.Println(f.String())
    }
}
```