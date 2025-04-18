---
sidebar_position: 5
---

import CodeEmbed from '@site/src/components/CodeEmbed';

# Create Model

This API lets you create a custom model on the Ollama server.

### Create a custom model from an existing model in the Ollama server

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/CreateModel.java" />

You would see these logs while the custom model is being created:

```
{"status":"using existing layer sha256:fad2a06e4cc705c2fa8bec5477ddb00dc0c859ac184c34dcc5586663774161ca"}
{"status":"using existing layer sha256:41c2cf8c272f6fb0080a97cd9d9bd7d4604072b80a0b10e7d65ca26ef5000c0c"}
{"status":"using existing layer sha256:1da0581fd4ce92dcf5a66b1da737cf215d8dcf25aa1b98b44443aaf7173155f5"}
{"status":"creating new layer sha256:941b69ca7dc2a85c053c38d9e8029c9df6224e545060954fa97587f87c044a64"}
{"status":"using existing layer sha256:f02dd72bb2423204352eabc5637b44d79d17f109fdb510a7c51455892aa2d216"}
{"status":"writing manifest"}
{"status":"success"}
```
Once created, you can see it when you use [list models](./list-models) API.

[Read more](https://github.com/ollama/ollama/blob/main/docs/api.md#create-a-model) about custom model creation and the parameters available for model creation.

[//]: # ()
[//]: # (### Example of a `Modelfile`)

[//]: # ()
[//]: # (```)

[//]: # (FROM llama2)

[//]: # (# sets the temperature to 1 [higher is more creative, lower is more coherent])

[//]: # (PARAMETER temperature 1)

[//]: # (# sets the context window size to 4096, this controls how many tokens the LLM can use as context to generate the next token)

[//]: # (PARAMETER num_ctx 4096)

[//]: # ()
[//]: # (# sets a custom system message to specify the behavior of the chat assistant)

[//]: # (SYSTEM You are Mario from super mario bros, acting as an assistant.)

[//]: # (```)

[//]: # ()
[//]: # (### Format of the `Modelfile`)

[//]: # ()
[//]: # (```modelfile)

[//]: # (# comment)

[//]: # (INSTRUCTION arguments)

[//]: # (```)

[//]: # ()
[//]: # (| Instruction                         | Description                                                    |)

[//]: # (|-------------------------------------|----------------------------------------------------------------|)

[//]: # (| [`FROM`]&#40;#from-required&#41; &#40;required&#41; | Defines the base model to use.                                 |)

[//]: # (| [`PARAMETER`]&#40;#parameter&#41;           | Sets the parameters for how Ollama will run the model.         |)

[//]: # (| [`TEMPLATE`]&#40;#template&#41;             | The full prompt template to be sent to the model.              |)

[//]: # (| [`SYSTEM`]&#40;#system&#41;                 | Specifies the system message that will be set in the template. |)

[//]: # (| [`ADAPTER`]&#40;#adapter&#41;               | Defines the &#40;Q&#41;LoRA adapters to apply to the model.            |)

[//]: # (| [`LICENSE`]&#40;#license&#41;               | Specifies the legal license.                                   |)

[//]: # ()
[//]: # (#### PARAMETER)

[//]: # ()
[//]: # (The `PARAMETER` instruction defines a parameter that can be set when the model is run.)

[//]: # ()
[//]: # (| Parameter      | Description                                                                                                                                                                                                                                             | Value Type | Example Usage        |)

[//]: # (|----------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------|----------------------|)

[//]: # (| mirostat       | Enable Mirostat sampling for controlling perplexity. &#40;default: 0, 0 = disabled, 1 = Mirostat, 2 = Mirostat 2.0&#41;                                                                                                                                         | int        | mirostat 0           |)

[//]: # (| mirostat_eta   | Influences how quickly the algorithm responds to feedback from the generated text. A lower learning rate will result in slower adjustments, while a higher learning rate will make the algorithm more responsive. &#40;Default: 0.1&#41;                        | float      | mirostat_eta 0.1     |)

[//]: # (| mirostat_tau   | Controls the balance between coherence and diversity of the output. A lower value will result in more focused and coherent text. &#40;Default: 5.0&#41;                                                                                                         | float      | mirostat_tau 5.0     |)

[//]: # (| num_ctx        | Sets the size of the context window used to generate the next token. &#40;Default: 2048&#41;                                                                                                                                                                    | int        | num_ctx 4096         |)

[//]: # (| num_gqa        | The number of GQA groups in the transformer layer. Required for some models, for example it is 8 for llama2:70b                                                                                                                                         | int        | num_gqa 1            |)

[//]: # (| num_gpu        | The number of layers to send to the GPU&#40;s&#41;. On macOS it defaults to 1 to enable metal support, 0 to disable.                                                                                                                                            | int        | num_gpu 50           |)

[//]: # (| num_thread     | Sets the number of threads to use during computation. By default, Ollama will detect this for optimal performance. It is recommended to set this value to the number of physical CPU cores your system has &#40;as opposed to the logical number of cores&#41;. | int        | num_thread 8         |)

[//]: # (| repeat_last_n  | Sets how far back for the model to look back to prevent repetition. &#40;Default: 64, 0 = disabled, -1 = num_ctx&#41;                                                                                                                                           | int        | repeat_last_n 64     |)

[//]: # (| repeat_penalty | Sets how strongly to penalize repetitions. A higher value &#40;e.g., 1.5&#41; will penalize repetitions more strongly, while a lower value &#40;e.g., 0.9&#41; will be more lenient. &#40;Default: 1.1&#41;                                                                     | float      | repeat_penalty 1.1   |)

[//]: # (| temperature    | The temperature of the model. Increasing the temperature will make the model answer more creatively. &#40;Default: 0.8&#41;                                                                                                                                     | float      | temperature 0.7      |)

[//]: # (| seed           | Sets the random number seed to use for generation. Setting this to a specific number will make the model generate the same text for the same prompt. &#40;Default: 0&#41;                                                                                       | int        | seed 42              |)

[//]: # (| stop           | Sets the stop sequences to use. When this pattern is encountered the LLM will stop generating text and return. Multiple stop patterns may be set by specifying multiple separate `stop` parameters in a modelfile.                                      | string     | stop "AI assistant:" |)

[//]: # (| tfs_z          | Tail free sampling is used to reduce the impact of less probable tokens from the output. A higher value &#40;e.g., 2.0&#41; will reduce the impact more, while a value of 1.0 disables this setting. &#40;default: 1&#41;                                               | float      | tfs_z 1              |)

[//]: # (| num_predict    | Maximum number of tokens to predict when generating text. &#40;Default: 128, -1 = infinite generation, -2 = fill context&#41;                                                                                                                                   | int        | num_predict 42       |)

[//]: # (| top_k          | Reduces the probability of generating nonsense. A higher value &#40;e.g. 100&#41; will give more diverse answers, while a lower value &#40;e.g. 10&#41; will be more conservative. &#40;Default: 40&#41;                                                                        | int        | top_k 40             |)

[//]: # (| top_p          | Works together with top-k. A higher value &#40;e.g., 0.95&#41; will lead to more diverse text, while a lower value &#40;e.g., 0.5&#41; will generate more focused and conservative text. &#40;Default: 0.9&#41;                                                                 | float      | top_p 0.9            |)

[//]: # ()
[//]: # (#### TEMPLATE)

[//]: # ()
[//]: # (`TEMPLATE` of the full prompt template to be passed into the model. It may include &#40;optionally&#41; a system message and a)

[//]: # (user's prompt. This is used to create a full custom prompt, and syntax may be model specific. You can usually find the)

[//]: # (template for a given model in the readme for that model.)

[//]: # ()
[//]: # (#### Template Variables)

[//]: # ()
[//]: # (| Variable        | Description                                                                                                   |)

[//]: # (|-----------------|---------------------------------------------------------------------------------------------------------------|)

[//]: # (| `{{ .System }}` | The system message used to specify custom behavior, this must also be set in the Modelfile as an instruction. |)

[//]: # (| `{{ .Prompt }}` | The incoming prompt, this is not specified in the model file and will be set based on input.                  |)

[//]: # (| `{{ .First }}`  | A boolean value used to render specific template information for the first generation of a session.           |)

[//]: # ()
[//]: # (```modelfile)

[//]: # (TEMPLATE """)

[//]: # ({{- if .First }})

[//]: # (### System:)

[//]: # ({{ .System }})

[//]: # ({{- end }})

[//]: # ()
[//]: # (### User:)

[//]: # ({{ .Prompt }})

[//]: # ()
[//]: # (### Response:)

[//]: # (""")

[//]: # ()
[//]: # (SYSTEM """<system message>""")

[//]: # (```)

[//]: # ()
[//]: # (### SYSTEM)

[//]: # ()
[//]: # (The `SYSTEM` instruction specifies the system message to be used in the template, if applicable.)

[//]: # ()
[//]: # (```modelfile)

[//]: # (SYSTEM """<system message>""")

[//]: # (```)

[//]: # ()
[//]: # (### ADAPTER)

[//]: # ()
[//]: # (The `ADAPTER` instruction specifies the LoRA adapter to apply to the base model. The value of this instruction should be)

[//]: # (an absolute path or a path relative to the Modelfile and the file must be in a GGML file format. The adapter should be)

[//]: # (tuned from the base model otherwise the behaviour is undefined.)

[//]: # ()
[//]: # (```modelfile)

[//]: # (ADAPTER ./ollama-lora.bin)

[//]: # (```)

[//]: # ()
[//]: # (### LICENSE)

[//]: # ()
[//]: # (The `LICENSE` instruction allows you to specify the legal license under which the model used with this Modelfile is)

[//]: # (shared or distributed.)

[//]: # ()
[//]: # (```modelfile)

[//]: # (LICENSE """)

[//]: # (<license text>)

[//]: # (""")

[//]: # (```)

[//]: # ()
[//]: # (## Notes)

[//]: # ()
[//]: # (- the **`Modelfile` is not case sensitive**. In the examples, uppercase instructions are used to make it easier to)

[//]: # (  distinguish it from arguments.)

[//]: # (- Instructions can be in any order. In the examples, the `FROM` instruction is first to keep it easily readable.)

[//]: # ()
[//]: # (Read more about Modelfile: https://github.com/jmorganca/ollama/blob/main/docs/modelfile.md)