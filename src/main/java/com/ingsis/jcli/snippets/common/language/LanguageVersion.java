package com.ingsis.jcli.snippets.common.language;

import com.ingsis.jcli.snippets.common.Generated;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Generated
@Embeddable
@Setter
@Getter
public class LanguageVersion {

  private String language;

  private String version;

  public LanguageVersion() {}

  public LanguageVersion(String language, String version) {
    this.language = language;
    this.version = version;
  }
}
