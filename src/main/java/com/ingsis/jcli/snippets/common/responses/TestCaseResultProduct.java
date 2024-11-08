package com.ingsis.jcli.snippets.common.responses;

import com.ingsis.jcli.snippets.common.requests.TestType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestCaseResultProduct {
  private Long testCaseId;
  private TestType type;
}
