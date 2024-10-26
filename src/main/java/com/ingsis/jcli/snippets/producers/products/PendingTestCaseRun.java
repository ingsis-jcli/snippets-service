package com.ingsis.jcli.snippets.producers.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PendingTestCaseRun(
    @JsonProperty("id") Long id,
    @JsonProperty("snippetName") String snippetName,
    @JsonProperty("url") String url,
    @JsonProperty("version") String version,
    @JsonProperty("input") List<String> input,
    @JsonProperty("output") List<String> output) {}
