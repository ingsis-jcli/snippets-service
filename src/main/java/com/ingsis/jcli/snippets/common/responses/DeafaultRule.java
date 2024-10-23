package com.ingsis.jcli.snippets.common.responses;

import lombok.Data;

@Data
public class DeafaultRule {
  private String name;
  private boolean isActive;
  private String value;

  public DeafaultRule(String name, boolean isActive, String value) {
    this.name = name;
    this.isActive = isActive;
    this.value = value;
  }
}
