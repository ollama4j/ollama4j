package io.github.ollama4j.models.request;

public abstract class Auth {
  /**
   * Get authentication header value.
   *
   * @return authentication header value
   */
  public abstract String getAuthHeaderValue();
}
