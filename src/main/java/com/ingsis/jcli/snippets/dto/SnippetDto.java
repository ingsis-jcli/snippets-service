package com.ingsis.jcli.snippets.dto;

import com.ingsis.jcli.snippets.common.Generated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Generated
@Data
public class SnippetDto {

  @NotBlank(message = "Name cannot be blank")
  private String name;

  private String description = "";

  @NotNull(message = "Content cannot be null")
  private String content;

  @NotBlank(message = "Has to specify program language")
  private String language;

  @NotBlank(message = "Has to specify program language's version")
  private String version;

  public SnippetDto() {}

  public SnippetDto(String name, String content, String language, String version) {
    this(name, "", content, language, version);
  }

  public SnippetDto(
      String name, String description, String content, String language, String version) {
    this.name = name;
    this.description = description;
    this.content = content;
    this.language = language;
    this.version = version;
  }
}
