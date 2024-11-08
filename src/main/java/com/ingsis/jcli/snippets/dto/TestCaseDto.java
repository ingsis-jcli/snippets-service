package com.ingsis.jcli.snippets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ingsis.jcli.snippets.common.requests.TestType;
import java.util.List;

public record TestCaseDto(
    @JsonProperty("name") String name,
    @JsonProperty("snippetId") Long snippetId,
    @JsonProperty("input") List<String> input,
    @JsonProperty("output") List<String> output,
    @JsonProperty("type") TestType type) {}
