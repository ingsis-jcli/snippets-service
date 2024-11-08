package com.ingsis.jcli.snippets.common.status;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Embeddable
@Data
public class Status {

  @NotNull
  @Column(columnDefinition = "INTEGER")
  private ProcessStatus formatting = ProcessStatus.NOT_STARTED;

  @NotNull
  @Column(columnDefinition = "INTEGER")
  private ProcessStatus linting = ProcessStatus.NOT_STARTED;

  public Status() {}
}
