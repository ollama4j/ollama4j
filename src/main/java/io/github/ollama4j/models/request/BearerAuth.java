package io.github.ollama4j.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BearerAuth extends Auth {
    private String bearerToken;

    /**
     * Get authentication header value.
     *
     * @return authentication header value with bearer token
     */
    public String getAuthHeaderValue() {
        return "Bearer " + bearerToken;
    }
}
