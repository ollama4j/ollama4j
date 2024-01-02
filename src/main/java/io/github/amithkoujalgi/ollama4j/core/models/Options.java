package io.github.amithkoujalgi.ollama4j.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Options {
  private Float temperature;
  private Float top_p;
}
