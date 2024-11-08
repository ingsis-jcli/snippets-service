package com.ingsis.jcli.snippets.common.responses;

import com.ingsis.jcli.snippets.common.status.ProcessStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnippetStatusUpdateProduct {
  private Long snippetId;
  private String operation;
  private ProcessStatus status;
}
