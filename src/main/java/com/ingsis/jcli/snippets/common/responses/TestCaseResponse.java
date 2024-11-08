package com.ingsis.jcli.snippets.common.responses;

import com.ingsis.jcli.snippets.common.requests.TestState;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestCaseResponse {
  private final Long id;
  private final Long snippetId;
  private final String name;
  private final List<String> input;
  private final List<String> output;
  private final TestState state;

  public TestCaseResponse(
      Long id,
      Long snippetId,
      String name,
      List<String> input,
      List<String> output,
      TestState state) {
    this.id = id;
    this.snippetId = snippetId;
    this.name = name;
    this.input = input;
    this.output = output;
    this.state = state;
  }
}
