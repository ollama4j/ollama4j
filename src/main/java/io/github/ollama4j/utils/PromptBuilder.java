package io.github.ollama4j.utils;

/**
 * The {@code PromptBuilder} class is used to construct prompt texts for language models (LLMs). It
 * provides methods for adding text, adding lines, adding separators, and building the final prompt.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * PromptBuilder promptBuilder = new PromptBuilder();
 * promptBuilder.add("This is a sample prompt for language models.")
 *              .addLine("You can add lines to provide context.")
 *              .addSeparator()
 *              .add("Feel free to customize as needed.");
 * String finalPrompt = promptBuilder.build();
 * System.out.println(finalPrompt);
 * }</pre>
 */
public class PromptBuilder {

  private final StringBuilder prompt;

  /** Constructs a new {@code PromptBuilder} with an empty prompt. */
  public PromptBuilder() {
    this.prompt = new StringBuilder();
  }

  /**
   * Appends the specified text to the prompt.
   *
   * @param text the text to be added to the prompt
   * @return a reference to this {@code PromptBuilder} instance for method chaining
   */
  public PromptBuilder add(String text) {
    prompt.append(text);
    return this;
  }

  /**
   * Appends the specified text followed by a newline character to the prompt.
   *
   * @param text the text to be added as a line to the prompt
   * @return a reference to this {@code PromptBuilder} instance for method chaining
   */
  public PromptBuilder addLine(String text) {
    prompt.append(text).append("\n");
    return this;
  }

  /**
   * Appends a separator line to the prompt. The separator is a newline followed by a line of
   * dashes.
   *
   * @return a reference to this {@code PromptBuilder} instance for method chaining
   */
  public PromptBuilder addSeparator() {
    prompt.append("\n--------------------------------------------------\n");
    return this;
  }

  /**
   * Builds and returns the final prompt as a string.
   *
   * @return the final prompt as a string
   */
  public String build() {
    return prompt.toString();
  }
}
