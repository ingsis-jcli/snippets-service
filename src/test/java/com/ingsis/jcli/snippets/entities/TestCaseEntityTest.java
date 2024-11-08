package com.ingsis.jcli.snippets.entities;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.ingsis.jcli.snippets.common.requests.TestState;
import com.ingsis.jcli.snippets.common.requests.TestType;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.models.TestCase;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class TestCaseEntityTest {

  @Test
  void testCreateTestCase() {
    Snippet snippet = new Snippet();
    String name = "Test Case 1";
    List<String> inputs = Arrays.asList("input1", "input2");
    List<String> outputs = Arrays.asList("output1", "output2");
    TestType type = TestType.VALID;
    TestState state = TestState.PENDING;

    TestCase testCase = new TestCase(snippet, name, inputs, outputs, type, state);

    assertEquals(name, testCase.getName());
    assertEquals(inputs, testCase.getInputs());
    assertEquals(outputs, testCase.getOutputs());
    assertEquals(type, testCase.getType());
    assertEquals(state, testCase.getState());
  }

  @Test
  void testGettersAndSetters() {
    Snippet snippet = new Snippet();
    TestCase testCase = new TestCase();
    testCase.setId(1L);
    testCase.setSnippet(snippet);
    testCase.setName("Test Case Name");
    testCase.setInputs(List.of("input1"));
    testCase.setOutputs(List.of("output1"));
    testCase.setType(TestType.VALID);
    testCase.setState(TestState.SUCCESS);

    assertEquals(1L, testCase.getId());
    assertEquals(snippet, testCase.getSnippet());
    assertEquals("Test Case Name", testCase.getName());
    assertEquals(List.of("input1"), testCase.getInputs());
    assertEquals(List.of("output1"), testCase.getOutputs());
    assertEquals(TestType.VALID, testCase.getType());
    assertEquals(TestState.SUCCESS, testCase.getState());
  }

  @Test
  void testDefaultConstructor() {
    TestCase testCase = new TestCase();
    assertNotNull(testCase);
  }

  @Test
  void testSettersForNullValues() {
    TestCase testCase = new TestCase();
    testCase.setSnippet(null);
    testCase.setName(null);
    testCase.setInputs(null);
    testCase.setOutputs(null);
    testCase.setType(null);
    testCase.setState(null);

    assertNull(testCase.getSnippet());
    assertNull(testCase.getName());
    assertNull(testCase.getInputs());
    assertNull(testCase.getOutputs());
    assertNull(testCase.getType());
    assertNull(testCase.getState());
  }
}
