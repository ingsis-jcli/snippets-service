package com.ingsis.jcli.snippets.models;

import com.ingsis.jcli.snippets.common.Generated;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;

@Generated
@Entity
@Data
public class Snippet {

  @SequenceGenerator(name = "snippet", sequenceName = "snippet_sequence")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "snippet")
  @Id
  private Long id;

  @NotBlank private String name;

  @NotBlank private String url;

  @NotBlank private String owner;

  @Embedded private LanguageVersion languageVersion;

  @OneToMany(mappedBy = "snippet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<TestCase> testCases;

  public Snippet() {}

  public Snippet(String name, String url, String owner, LanguageVersion languageVersion) {
    this.name = name;
    this.url = url;
    this.owner = owner;
    this.languageVersion = languageVersion;
  }
}
