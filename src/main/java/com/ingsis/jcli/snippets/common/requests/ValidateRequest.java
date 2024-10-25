package com.ingsis.jcli.snippets.common.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ValidateRequest(
    @JsonProperty("name") String name,
    @JsonProperty("url") String url,
    @JsonProperty("version") String version) {}
