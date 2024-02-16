package io.github.amithkoujalgi.ollama4j.core.models;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.github.amithkoujalgi.ollama4j.core.utils.BooleanToJsonFormatFlagSerializer;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public abstract class OllamaCommonRequestModel {
  
  @NonNull
  protected String model;  
  @JsonSerialize(using = BooleanToJsonFormatFlagSerializer.class)
  protected boolean returnFormatJson;
  protected Map<String, Object> options;
  protected String template;
  protected boolean stream;
  @JsonProperty(value = "keep_alive")
  protected String keepAlive;
}
