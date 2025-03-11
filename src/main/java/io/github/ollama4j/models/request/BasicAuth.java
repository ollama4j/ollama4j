package io.github.ollama4j.models.request;

import java.util.Base64;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class BasicAuth extends Auth {
  private String username;
  private String password;

  /**
   * Get basic authentication header value.
   *
   * @return basic authentication header value (encoded credentials)
   */
  public String getAuthHeaderValue() {
      final String credentialsToEncode = this.getUsername() + ":" + this.getPassword();
      return "Basic " + Base64.getEncoder().encodeToString(credentialsToEncode.getBytes());
  }
}
