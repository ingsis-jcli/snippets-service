package com.ingsis.jcli.snippets.common.language;

import com.ingsis.jcli.snippets.common.Generated;

@Generated
public interface LanguageResponse {

  default boolean hasError() {
    return false;
  }

  default String getError() {
    return null;
  }
}
