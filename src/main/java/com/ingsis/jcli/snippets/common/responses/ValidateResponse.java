package com.ingsis.jcli.snippets.common.responses;

import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Generated
@Getter
@NoArgsConstructor
public class ValidateResponse {
  private String error;

  public ValidateResponse(String error) {
    this.error = error;
  }

  public boolean isValid() {
    return error == null;
  }
}
