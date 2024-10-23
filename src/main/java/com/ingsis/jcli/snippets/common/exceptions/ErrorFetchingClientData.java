package com.ingsis.jcli.snippets.common.exceptions;

import com.ingsis.jcli.snippets.common.language.LanguageVersion;

public class ErrorFetchingClientData extends RuntimeException {
  private final LanguageVersion languageVersion;
  private final String error;

  public ErrorFetchingClientData(String error, LanguageVersion languageVersion) {
    super("Error getting data from the client " + languageVersion.toString() + " : " + error);
    this.error = error;
    this.languageVersion = languageVersion;
  }
}
