package com.ingsis.jcli.snippets.common.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ErrorResponse(@JsonProperty("error") String error) {}
