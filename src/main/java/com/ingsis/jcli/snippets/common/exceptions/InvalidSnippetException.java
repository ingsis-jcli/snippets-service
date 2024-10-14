package com.ingsis.jcli.snippets.common.exceptions;

import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import lombok.Getter;

@Getter
public class InvalidSnippetException extends RuntimeException {
  private final String message;
  private final LanguageVersion languageVersion;

  public InvalidSnippetException(String message, LanguageVersion languageVersion) {
    super("Error validating snippet: " + message);
    this.message = message;
    this.languageVersion = languageVersion;
  }
}
