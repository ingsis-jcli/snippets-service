package com.ingsis.jcli.snippets.producers.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ingsis.jcli.snippets.common.requests.RuleDto;

import java.util.List;

public record PendingSnippetLint(
  @JsonProperty("snippetId") Long snippetId,
  @JsonProperty("name") String name,
  @JsonProperty("url") String url,
  @JsonProperty("rules") List<RuleDto> rules,
  @JsonProperty("version") String version) {}
