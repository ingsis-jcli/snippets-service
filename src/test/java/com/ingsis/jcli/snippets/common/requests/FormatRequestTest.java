package com.ingsis.jcli.snippets.common.requests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class FormatRequestTest {

  @Test
  void testFormatRequestCreation() {
    RuleDto rule = new RuleDto(true, "rule1", "value1");
    List<RuleDto> rules = Collections.singletonList(rule);

    String expectedName = "Test Format";
    String expectedUrl = "http://example.com";
    String expectedVersion = "1.0";
    FormatRequest formatRequest =
        new FormatRequest(expectedName, expectedUrl, rules, expectedVersion);

    assertNotNull(formatRequest);
    assertEquals(expectedName, formatRequest.name());
    assertEquals(expectedUrl, formatRequest.url());
    assertEquals(rules, formatRequest.rules());
    assertEquals(expectedVersion, formatRequest.version());
  }
}
