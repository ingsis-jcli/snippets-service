package com.ingsis.jcli.snippets.common.exceptions;

import lombok.Getter;

@Getter
public class NoSuchLanguageException extends RuntimeException {

  private final String language;

  public NoSuchLanguageException(String language) {
    super("No such language: " + language);
    this.language = language;
  }
}
