package com.ingsis.jcli.snippets.consumers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ingsis.jcli.snippets.common.requests.TestType;
import com.ingsis.jcli.snippets.common.responses.TestCaseResultProduct;
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
}
