package com.ingsis.jcli.snippets.common.language;

import com.ingsis.jcli.snippets.common.Generated;

@Generated
public class LanguageError implements LanguageResponse {

  private final String errorMessage;

  public LanguageError(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public boolean hasError() {
    return true;
  }

  @Override
  public String getError() {
    return errorMessage;
  }
}
