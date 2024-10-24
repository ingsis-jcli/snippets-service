package com.ingsis.jcli.snippets.common.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DefaultRule {
  @JsonProperty("name")
  private String name;

  @JsonProperty("isActive")
  private boolean isActive;

  @JsonProperty("value")
  private String value;

  public DefaultRule(String name, boolean isActive, String value) {
    this.name = name;
    this.isActive = isActive;
    this.value = value;
  }
}
