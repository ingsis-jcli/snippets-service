package com.ingsis.jcli.snippets.common.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ingsis.jcli.snippets.common.status.ProcessStatus;

public record FormatResponse(
    @JsonProperty("content") String content, @JsonProperty("status") ProcessStatus status) {}
