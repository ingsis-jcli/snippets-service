package com.ingsis.jcli.snippets.common.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ErrorResponse(@JsonProperty("error") String error) {

  public ErrorResponse() {
    this(null);
  }

  public boolean hasError() {
    return error != null;
  }
}
