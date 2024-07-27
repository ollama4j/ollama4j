---
sidebar_position: 1
---

# Options Builder

This lets you build options for the `ask()` API.

Following are the parameters supported by Ollama:

| Parameter      | Description                                                                                                                                                                                                                                             | Value Type | Example Usage        |
|----------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------|----------------------|
| mirostat       | Enable Mirostat sampling for controlling perplexity. (default: 0, 0 = disabled, 1 = Mirostat, 2 = Mirostat 2.0)                                                                                                                                         | int        | mirostat 0           |
| mirostat_eta   | Influences how quickly the algorithm responds to feedback from the generated text. A lower learning rate will result in slower adjustments, while a higher learning rate will make the algorithm more responsive. (Default: 0.1)                        | float      | mirostat_eta 0.1     |
| mirostat_tau   | Controls the balance between coherence and diversity of the output. A lower value will result in more focused and coherent text. (Default: 5.0)                                                                                                         | float      | mirostat_tau 5.0     |
| num_ctx        | Sets the size of the context window used to generate the next token. (Default: 2048)                                                                                                                                                                    | int        | num_ctx 4096         |
| num_gqa        | The number of GQA groups in the transformer layer. Required for some models, for example it is 8 for llama2:70b                                                                                                                                         | int        | num_gqa 1            |
| num_gpu        | The number of layers to send to the GPU(s). On macOS it defaults to 1 to enable metal support, 0 to disable.                                                                                                                                            | int        | num_gpu 50           |
| num_thread     | Sets the number of threads to use during computation. By default, Ollama will detect this for optimal performance. It is recommended to set this value to the number of physical CPU cores your system has (as opposed to the logical number of cores). | int        | num_thread 8         |
| repeat_last_n  | Sets how far back for the model to look back to prevent repetition. (Default: 64, 0 = disabled, -1 = num_ctx)                                                                                                                                           | int        | repeat_last_n 64     |
| repeat_penalty | Sets how strongly to penalize repetitions. A higher value (e.g., 1.5) will penalize repetitions more strongly, while a lower value (e.g., 0.9) will be more lenient. (Default: 1.1)                                                                     | float      | repeat_penalty 1.1   |
| temperature    | The temperature of the model. Increasing the temperature will make the model answer more creatively. (Default: 0.8)                                                                                                                                     | float      | temperature 0.7      |
| seed           | Sets the random number seed to use for generation. Setting this to a specific number will make the model generate the same text for the same prompt. (Default: 0)                                                                                       | int        | seed 42              |
| stop           | Sets the stop sequences to use. When this pattern is encountered the LLM will stop generating text and return. Multiple stop patterns may be set by specifying multiple separate `stop` parameters in a modelfile.                                      | string     | stop "AI assistant:" |
| tfs_z          | Tail free sampling is used to reduce the impact of less probable tokens from the output. A higher value (e.g., 2.0) will reduce the impact more, while a value of 1.0 disables this setting. (default: 1)                                               | float      | tfs_z 1              |
| num_predict    | Maximum number of tokens to predict when generating text. (Default: 128, -1 = infinite generation, -2 = fill context)                                                                                                                                   | int        | num_predict 42       |
| top_k          | Reduces the probability of generating nonsense. A higher value (e.g. 100) will give more diverse answers, while a lower value (e.g. 10) will be more conservative. (Default: 40)                                                                        | int        | top_k 40             |
| top_p          | Works together with top-k. A higher value (e.g., 0.95) will lead to more diverse text, while a lower value (e.g., 0.5) will generate more focused and conservative text. (Default: 0.9)                                                                 | float      | top_p 0.9            |

Link to [source](https://github.com/jmorganca/ollama/blob/main/docs/modelfile.md#valid-parameters-and-values).

Also, see how to set those Ollama parameters using
the `OptionsBuilder`
from [javadoc](https://ollama4j.github.io/ollama4j/apidocs/io/github/ollama4j/ollama4j/core/utils/OptionsBuilder.html).

## Build an empty `Options` object

```java
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.utils.Options;
import io.github.ollama4j.utils.OptionsBuilder;

public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        Options options = new OptionsBuilder().build();
    }
}
```

## Build the `Options` object with values

```java
import io.github.ollama4j.utils.Options;
import io.github.ollama4j.utils.OptionsBuilder;

public class Main {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        Options options =
                new OptionsBuilder()
                        .setMirostat(10)
                        .setMirostatEta(0.5f)
                        .setNumGpu(2)
                        .setTemperature(1.5f)
                        .build();
    }
}
```
