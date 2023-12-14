package io.github.amithkoujalgi.ollama4j.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModelMeta {

  @JsonProperty("format")
  private String format;

  @JsonProperty("family")
  private String family;

  @JsonProperty("families")
  private String[] families;

  @JsonProperty("parameter_size")
  private String parameterSize;

  @JsonProperty("quantization_level")
  private String quantizationLevel;

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getFamily() {
    return family;
  }

  public void setFamily(String family) {
    this.family = family;
  }

  public String[] getFamilies() {
    return families;
  }

  public void setFamilies(String[] families) {
    this.families = families;
  }

  public String getParameterSize() {
    return parameterSize;
  }

  public void setParameterSize(String parameterSize) {
    this.parameterSize = parameterSize;
  }

  public String getQuantizationLevel() {
    return quantizationLevel;
  }

  public void setQuantizationLevel(String quantizationLevel) {
    this.quantizationLevel = quantizationLevel;
  }
}
