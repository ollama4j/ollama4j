package io.github.ollama4j.utils;

import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Interface to represent a OllamaRequest as HTTP-Request Body via {@link BodyPublishers}.
 */
public interface OllamaRequestBody {
    
    /**
     * Transforms the OllamaRequest Object to a JSON Object via Jackson.
     * 
     * @return JSON representation of a OllamaRequest
     */
    @JsonIgnore
    default BodyPublisher getBodyPublisher(){
                try {
          return BodyPublishers.ofString(
                      Utils.getObjectMapper().writeValueAsString(this));
        } catch (JsonProcessingException e) {
          throw new IllegalArgumentException("Request not Body convertible.",e);
        }
    }
}
