package io.github.ollama4j.models.request;

import java.util.Base64;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicAuth {
  private String username;
  private String password;

  /**
   * Get basic authentication header value.
   *
   * @return basic authentication header value (encoded credentials)
   */
  public String getBasicAuthHeaderValue() {
      final String credentialsToEncode = this.getUsername() + ":" + this.getPassword();
      return "Basic " + Base64.getEncoder().encodeToString(credentialsToEncode.getBytes());
  }
}
