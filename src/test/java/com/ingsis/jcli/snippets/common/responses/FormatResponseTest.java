package com.ingsis.jcli.snippets.common.responses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ingsis.jcli.snippets.common.status.ProcessStatus;
import org.junit.jupiter.api.Test;

class FormatResponseTest {

  @Test
  void testFormatResponseCreation() {
    ProcessStatus status = ProcessStatus.COMPLIANT;
    String expectedContent = "formatted code content";
    FormatResponse formatResponse = new FormatResponse(expectedContent, status);
    assertNotNull(formatResponse);
    assertEquals(expectedContent, formatResponse.content());
    assertEquals(status, formatResponse.status());
  }
}
