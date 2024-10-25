package com.ingsis.jcli.snippets.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    TestCase testCase = new TestCase(snippet, name, inputs, outputs, type);

    assertEquals(name, testCase.getName());
    assertEquals(inputs, testCase.getInputs());
    assertEquals(outputs, testCase.getOutputs());
    assertEquals(type, testCase.getType());
  }
}
