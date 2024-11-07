package com.ingsis.jcli.snippets.common.responses;

import com.ingsis.jcli.snippets.common.status.ProcessStatus;
import lombok.Getter;

public class SnippetResponse {
  @Getter private Long id;
  @Getter private String name;
  @Getter private String content;
  @Getter private String language;
  @Getter private String version;
  @Getter private String extension;
  @Getter private ProcessStatus compliance;
  @Getter private String author;

  public SnippetResponse(
      Long id,
      String name,
      String content,
      String language,
      String version,
      String extension,
      ProcessStatus compliance,
      String author) {
    this.id = id;
    this.name = name;
    this.content = content;
    this.language = language;
    this.version = version;
    this.extension = extension;
    this.compliance = compliance;
    this.author = author;
  }
}
