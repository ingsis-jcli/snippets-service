package com.ingsis.jcli.snippets.common;

import com.ingsis.jcli.snippets.models.Language;
import com.ingsis.jcli.snippets.models.Version;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Setter;

@Generated
@Embeddable
@Setter
public class LanguageVersion {

  @ManyToOne
  @JoinColumn(name = "language_id", nullable = false)
  private Language language;

  @ManyToOne
  @JoinColumn(name = "version_id", nullable = false)
  private Version version;

  public LanguageVersion() {}

  public LanguageVersion(Language language, Version version) {
    this.language = language;
    this.version = version;
  }
}
