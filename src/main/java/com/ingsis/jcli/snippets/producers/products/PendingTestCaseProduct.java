package com.ingsis.jcli.snippets.producers.products;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PendingTestCaseProduct(
    @JsonProperty("id") Long id,
    @JsonProperty("snippetName") String snippetName,
    @JsonProperty("url") String url,
    @JsonProperty("version") String version,
    @JsonProperty("input") List<String> input,
    @JsonProperty("output") List<String> output)
    implements PendingTestCase {}
