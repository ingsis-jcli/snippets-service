package com.ingsis.jcli.snippets.common.exceptions;

import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import lombok.Getter;

@Getter
public class InvalidSnippetException extends RuntimeException {
  private final String error;
  private final LanguageVersion languageVersion;

  public InvalidSnippetException(String error, LanguageVersion languageVersion) {
    super("Invalid snippet exception : " + error);
    this.error = error;
    this.languageVersion = languageVersion;
  }
}
