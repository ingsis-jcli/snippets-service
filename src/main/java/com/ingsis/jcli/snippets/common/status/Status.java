package com.ingsis.jcli.snippets.common.status;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Embeddable
@Data
public class Status {
  
  @NotNull
  private ProcessStatus formatting = ProcessStatus.NOT_STARTED;
  
  @NotNull
  private ProcessStatus linting = ProcessStatus.NOT_STARTED;
  
  protected Status() {}
}
