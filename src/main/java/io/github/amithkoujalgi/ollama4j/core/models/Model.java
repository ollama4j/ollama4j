package io.github.amithkoujalgi.ollama4j.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Model {

  private String name;
  private String model;
  @JsonProperty("modified_at")
  private String modifiedAt;
  private String digest;
  private long size;
  @JsonProperty("details")
  private ModelMeta modelMeta;


  /**
   * Returns the model name without its version
   *
   * @return model name
   */
  public String getModelName() {
    return name.split(":")[0];
  }

  /**
   * Returns the model version without its name
   *
   * @return model version
   */
  public String getModelVersion() {
    return name.split(":")[1];
  }

}
