package com.ingsis.jcli.snippets.models;

import com.ingsis.jcli.snippets.common.LanguageVersion;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

@Entity
@Data
public class Snippet {

  @SequenceGenerator(
      name = "snippet",
      sequenceName = "snippet_sequence"
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "snippet"
  )
  @Id
  private Long id;

  private String name;

  private String url;

  private Long owner;

  @Embedded
  private LanguageVersion languageVersion;

  public Snippet() {}

  public Snippet(String name, String url, Long owner) {
    this.name = name;
    this.url = url;
    this.owner = owner;
  }
}
