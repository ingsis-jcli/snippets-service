package com.ingsis.jcli.snippets.common.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ingsis.jcli.snippets.models.Rule;

public record RuleDto(
    @JsonProperty("isActive") boolean isActive,
    @JsonProperty("name") String name,
    @JsonProperty("value") String value) {

  public static RuleDto of(Rule rule) {
    return new RuleDto(rule.isActive(), rule.getName(), rule.getValue());
  }
}