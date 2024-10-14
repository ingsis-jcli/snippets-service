package com.ingsis.jcli.snippets.common.language;

import com.ingsis.jcli.snippets.common.Generated;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Generated
@Embeddable
@Data
public class LanguageVersion {

  @NotBlank private String language;

  @NotBlank private String version;

  public LanguageVersion() {}

  public LanguageVersion(String language, String version) {
    this.language = language;
    this.version = version;
  }
}
