package com.ingsis.jcli.snippets.common.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ingsis.jcli.snippets.models.Rule;

public record RuleDto(
  @JsonProperty("isActive") boolean isActive,
  @JsonProperty("name") String name,
  @JsonProperty("value") String value) {
}
