package com.ingsis.jcli.snippets.consumers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ingsis.jcli.snippets.common.requests.TestType;
import com.ingsis.jcli.snippets.common.responses.SnippetStatusUpdateProduct;
import com.ingsis.jcli.snippets.common.responses.TestCaseResultProduct;
import com.ingsis.jcli.snippets.common.status.ProcessStatus;
import org.junit.jupiter.api.Test;

public class DeserializerUtilTest {

  @Test
  void whenValidJsonProvided_thenCorrectlyDeserializeToTestCaseResultProduct() {
    String json =
        """
      {
        "testCaseId": 12345,
        "type": "VALID"
      }
        """;

    TestCaseResultProduct result = DeserializerUtil.deserializeIntoTestResult(json);
    assertNotNull(result);
    assertEquals(12345L, result.getTestCaseId());
    assertEquals(TestType.VALID, result.getType());
  }

  @Test
  void whenValidJsonProvided_thenCorrectlyDeserializeToSnippetStatusUpdateProduct() {
    String json =
        """
      {
        "snippetId": 67890,
        "operation": "FORMAT",
        "status": "NON_COMPLIANT"
      }
        """;

    SnippetStatusUpdateProduct result = DeserializerUtil.deserializeIntoSnippetStatusUpdate(json);
    assertNotNull(result);
    assertEquals(67890L, result.getSnippetId());
    assertEquals("FORMAT", result.getOperation());
    assertEquals(ProcessStatus.NON_COMPLIANT, result.getStatus());
  }
}
