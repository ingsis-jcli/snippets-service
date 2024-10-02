package com.ingsis.jcli.snippets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SnippetDto {

  @NotBlank(message = "Name cannot be blank")
  private String name;

  @NotNull(message = "Content cannot be null")
  private String content;

  @NotNull(message = "Has to have an owner")
  private Long owner;

  public SnippetDto() {}

  public SnippetDto(String name, String content, Long owner) {
    this.name = name;
    this.content = content;
    this.owner = owner;
  }
}
