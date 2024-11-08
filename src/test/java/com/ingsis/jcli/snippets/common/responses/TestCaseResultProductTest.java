package com.ingsis.jcli.snippets.common.responses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.ingsis.jcli.snippets.common.requests.TestType;
import org.junit.jupiter.api.Test;

public class TestCaseResultProductTest {

  @Test
  void whenSettingValues_thenValuesAreCorrectlyRetrieved() {
    TestCaseResultProduct product = new TestCaseResultProduct();
    Long expectedId = 123L;
    TestType expectedType = TestType.VALID;

    product.setTestCaseId(expectedId);
    product.setType(expectedType);

    assertEquals(expectedId, product.getTestCaseId());
    assertEquals(expectedType, product.getType());
  }

  @Test
  void whenSettingNullType_thenTypeIsNull() {
    TestCaseResultProduct product = new TestCaseResultProduct();

    product.setType(null);
    assertNull(product.getType());
  }

  @Test
  void whenCreatingInstance_thenDefaultValuesAreCorrect() {
    TestCaseResultProduct product = new TestCaseResultProduct();
    assertNull(product.getTestCaseId());
    assertNull(product.getType());
  }
}
