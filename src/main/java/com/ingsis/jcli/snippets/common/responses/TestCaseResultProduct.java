package com.ingsis.jcli.snippets.common.responses;

import com.ingsis.jcli.snippets.common.requests.TestType;
import lombok.Getter;

@Getter
public class TestCaseResultProduct {
  private Long testCaseId;
  private TestType type;
}
