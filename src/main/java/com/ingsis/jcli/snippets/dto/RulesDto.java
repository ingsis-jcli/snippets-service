package com.ingsis.jcli.snippets.dto;

import com.ingsis.jcli.snippets.common.Generated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Generated
@Data
public class RulesDto {
  @NotNull(message = "Has to specific a userId")
  private String userId;

  @NotBlank(message = "Has to specify program language")
  private String language;

  @NotBlank(message = "Has to specify program language's version")
  private String version;

  public RulesDto() {}

  public RulesDto(String userId, String language, String version) {
    this.userId = userId;
    this.language = language;
    this.version = version;
  }
}
