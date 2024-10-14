package com.ingsis.jcli.snippets.common.language;

public interface LanguageResponse {

  default boolean hasError() {
    return false;
  }

  default String getError() {
    return null;
  }
}
