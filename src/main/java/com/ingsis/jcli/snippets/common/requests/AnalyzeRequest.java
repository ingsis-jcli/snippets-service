package com.ingsis.jcli.snippets.common.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AnalyzeRequest(
    @JsonProperty("name") String name,
    @JsonProperty("url") String url,
    @JsonProperty("rules") List<RuleDto> rules,
    @JsonProperty("version") String version) {}
