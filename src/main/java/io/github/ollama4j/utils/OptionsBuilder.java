package io.github.ollama4j.utils;

import java.io.IOException;
import java.util.HashMap;

/** Builder class for creating options for Ollama model. */
public class OptionsBuilder {

  private final Options options;

  /** Constructs a new OptionsBuilder with an empty options map. */
  public OptionsBuilder() {
    this.options = new Options(new HashMap<>());
  }

  /**
   * Enable Mirostat sampling for controlling perplexity. (default: 0, 0 = disabled, 1 = Mirostat, 2
   * = Mirostat 2.0)
   *
   * @param value The value for the "mirostat" parameter.
   * @return The updated OptionsBuilder.
   */
  public OptionsBuilder setMirostat(int value) {
    options.getOptionsMap().put("mirostat", value);
    return this;
  }

  /**
   * Influences how quickly the algorithm responds to feedback from the generated text. A lower
   * learning rate will result in slower adjustments, while a higher learning rate will make the
   * algorithm more responsive. (Default: 0.1)
   *
   * @param value The value for the "mirostat_eta" parameter.
   * @return The updated OptionsBuilder.
   */
  public OptionsBuilder setMirostatEta(float value) {
    options.getOptionsMap().put("mirostat_eta", value);
    return this;
  }

  /**
   * Controls the balance between coherence and diversity of the output. A lower value will result
   * in more focused and coherent text. (Default: 5.0)
   *
   * @param value The value for the "mirostat_tau" parameter.
   * @return The updated OptionsBuilder.
   */
  public OptionsBuilder setMirostatTau(float value) {
    options.getOptionsMap().put("mirostat_tau", value);
    return this;
  }

  /**
   * Sets the size of the context window used to generate the next token. (Default: 2048)
   *
   * @param value The value for the "num_ctx" parameter.
   * @return The updated OptionsBuilder.
   */
  public OptionsBuilder setNumCtx(int value) {
    options.getOptionsMap().put("num_ctx", value);
    return this;
  }

  /**
   * The number of GQA groups in the transformer layer. Required for some models, for example, it is
   * 8 for llama2:70b.
   *
   * @param value The value for the "num_gqa" parameter.
   * @return The updated OptionsBuilder.
   */
  public OptionsBuilder setNumGqa(int value) {
    options.getOptionsMap().put("num_gqa", value);
    return this;
  }

  /**
   * The number of layers to send to the GPU(s). On macOS it defaults to 1 to enable metal support,
   * 0 to disable.
   *
   * @param value The value for the "num_gpu" parameter.
   * @return The updated OptionsBuilder.
   */
  public OptionsBuilder setNumGpu(int value) {
    options.getOptionsMap().put("num_gpu", value);
    return this;
  }

  /**
   * Sets the number of threads to use during computation. By default, Ollama will detect this for
   * optimal performance. It is recommended to set this value to the number of physical CPU cores
   * your system has (as opposed to the logical number of cores).
   *
   * @param value The value for the "num_thread" parameter.
   * @return The updated OptionsBuilder.
   */
  public OptionsBuilder setNumThread(int value) {
    options.getOptionsMap().put("num_thread", value);
    return this;
  }

  /**
   * Sets how far back for the model to look back to prevent repetition. (Default: 64, 0 = disabled,
   * -1 = num_ctx)
   *
   * @param value The value for the "repeat_last_n" parameter.
   * @return The updated OptionsBuilder.
   */
  public OptionsBuilder setRepeatLastN(int value) {
    options.getOptionsMap().put("repeat_last_n", value);
    return this;
  }

  /**
   * Sets how strongly to penalize repetitions. A higher value (e.g., 1.5) will penalize repetitions
   * more strongly, while a lower value (e.g., 0.9) will be more lenient. (Default: 1.1)
   *
   * @param value The value for the "repeat_penalty" parameter.
   * @return The updated OptionsBuilder.
   */
  public OptionsBuilder setRepeatPenalty(float value) {
    options.getOptionsMap().put("repeat_penalty", value);
    return this;
  }

  /**
   * The temperature of the model. Increasing the temperature will make the model answer more
   * creatively. (Default: 0.8)
   *
   * @param value The value for the "temperature" parameter.
   * @return The updated OptionsBuilder.
   */
  public OptionsBuilder setTemperature(float value) {
    options.getOptionsMap().put("temperature", value);
    return this;
  }

  /**
   * Sets the random number seed to use for generation. Setting this to a specific number will make
   * the model generate the same text for the same prompt. (Default: 0)
   *
   * @param value The value for the "seed" parameter.
   * @return The updated OptionsBuilder.
   */
  public OptionsBuilder setSeed(int value) {
    options.getOptionsMap().put("seed", value);
    return this;
  }

  /**
   * Sets the stop sequences to use. When this pattern is encountered the LLM will stop generating
   * text and return. Multiple stop patterns may be set by specifying multiple separate `stop`
   * parameters in a modelfile.
   *
   * @param value The value for the "stop" parameter.
   * @return The updated OptionsBuilder.
   */
  public OptionsBuilder setStop(String value) {
    options.getOptionsMap().put("stop", value);
    return this;
  }

  /**
   * Tail free sampling is used to reduce the impact of less probable tokens from the output. A
   * higher value (e.g., 2.0) will reduce the impact more, while a value of 1.0 disables this
   * setting. (default: 1)
   *
   * @param value The value for the "tfs_z" parameter.
   * @return The updated OptionsBuilder.
   */
  public OptionsBuilder setTfsZ(float value) {
    options.getOptionsMap().put("tfs_z", value);
    return this;
  }

  /**
   * Maximum number of tokens to predict when generating text. (Default: 128, -1 = infinite
   * generation, -2 = fill context)
   *
   * @param value The value for the "num_predict" parameter.
   * @return The updated OptionsBuilder.
   */
  public OptionsBuilder setNumPredict(int value) {
    options.getOptionsMap().put("num_predict", value);
    return this;
  }

  /**
   * Reduces the probability of generating nonsense. A higher value (e.g. 100) will give more
   * diverse answers, while a lower value (e.g. 10) will be more conservative. (Default: 40)
   *
   * @param value The value for the "top_k" parameter.
   * @return The updated OptionsBuilder.
   */
  public OptionsBuilder setTopK(int value) {
    options.getOptionsMap().put("top_k", value);
    return this;
  }

  /**
   * Works together with top-k. A higher value (e.g., 0.95) will lead to more diverse text, while a
   * lower value (e.g., 0.5) will generate more focused and conservative text. (Default: 0.9)
   *
   * @param value The value for the "top_p" parameter.
   * @return The updated OptionsBuilder.
   */
  public OptionsBuilder setTopP(float value) {
    options.getOptionsMap().put("top_p", value);
    return this;
  }

  /**
   * Alternative to the top_p, and aims to ensure a balance of qualityand variety. The parameter p
   * represents the minimum probability for a token to be considered, relative to the probability
   * of the most likely token. For example, with p=0.05 and the most likely token having a
   * probability of 0.9, logits with a value less than 0.045 are filtered out. (Default: 0.0)
   */
  public OptionsBuilder setMinP(float value) {
    options.getOptionsMap().put("min_p", value);
    return this;
  }

  /**
   * Allows passing an option not formally supported by the library
   * @param name The option name for the parameter.
   * @param value The value for the "{name}" parameter.
   * @return The updated OptionsBuilder.
   * @throws IllegalArgumentException if parameter has an unsupported type
   */
  public OptionsBuilder setCustomOption(String name, Object value) throws IllegalArgumentException {
    if (!(value instanceof Integer || value instanceof Float || value instanceof String)) {
      throw new IllegalArgumentException("Invalid type for parameter. Allowed types are: Integer, Float, or String.");
    }
    options.getOptionsMap().put(name, value);
    return this;
  }



  /**
   * Builds the options map.
   *
   * @return The populated options map.
   */
  public Options build() {
    return options;
  }


}
