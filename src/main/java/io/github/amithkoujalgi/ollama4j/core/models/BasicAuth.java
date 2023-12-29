package io.github.amithkoujalgi.ollama4j.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicAuth {
  private String username;
  private String password;
}
