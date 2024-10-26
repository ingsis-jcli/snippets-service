package com.ingsis.jcli.snippets.producers.products;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PendingTestCaseRunTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testPendingTestCaseRunCreation() {
    Long id = 1L;
    String snippetName = "test-snippet";
    String url = "http://example.com/snippet";
    String version = "1.0";
    List<String> input = List.of("input1", "input2");
    List<String> output = List.of("output1");

    PendingTestCaseProduct pendingTestCaseRun =
        new PendingTestCaseProduct(id, snippetName, url, version, input, output);

    assertEquals(id, pendingTestCaseRun.id());
    assertEquals(snippetName, pendingTestCaseRun.snippetName());
    assertEquals(url, pendingTestCaseRun.url());
    assertEquals(version, pendingTestCaseRun.version());
    assertEquals(input, pendingTestCaseRun.input());
    assertEquals(output, pendingTestCaseRun.output());
  }

  @Test
  void testPendingTestCaseRunSerialization() throws Exception {
    PendingTestCaseProduct pendingTestCaseRun =
        new PendingTestCaseProduct(
            1L,
            "test-snippet",
            "http://example.com/snippet",
            "1.0",
            List.of("input1", "input2"),
            List.of("output1"));

    String json = objectMapper.writeValueAsString(pendingTestCaseRun);
    System.out.println("Serialized JSON: " + json);

    assertNotNull(json);
    String expectedJson =
        """
      {
        "id":1,
        "snippetName":"test-snippet",
        "url":"http://example.com/snippet",
        "version":"1.0",
        "input":["input1","input2"],
        "output":["output1"]
      }
        """
            .replaceAll("\\s", "");

    assertEquals(expectedJson, json.replaceAll("\\s", ""));
  }

  @Test
  void testPendingTestCaseRunDeserialization() throws Exception {
    String json =
        """
      {
        "id": 1,
        "snippetName": "test-snippet",
        "url": "http://example.com/snippet",
        "version": "1.0",
        "input": ["input1", "input2"],
        "output": ["output1"]
      }
        """;

    PendingTestCaseProduct pendingTestCaseRun =
        objectMapper.readValue(json, PendingTestCaseProduct.class);
    assertNotNull(pendingTestCaseRun);
    assertEquals(1L, pendingTestCaseRun.id());
    assertEquals("test-snippet", pendingTestCaseRun.snippetName());
    assertEquals("http://example.com/snippet", pendingTestCaseRun.url());
    assertEquals("1.0", pendingTestCaseRun.version());
    assertEquals(List.of("input1", "input2"), pendingTestCaseRun.input());
    assertEquals(List.of("output1"), pendingTestCaseRun.output());
  }
}
