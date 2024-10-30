package com.ingsis.jcli.snippets.common.requests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ingsis.jcli.snippets.models.Rule;
import org.junit.jupiter.api.Test;

class RuleDtoTest {

  @Test
  void testOf_WithStringValue() {
    Rule rule = new Rule("TestRule", "stringValue", true);
    rule.setValue("stringValue");
    rule.setNumericValue(null);

    RuleDto ruleDto = RuleDto.of(rule);

    assertNotNull(ruleDto);
    assertTrue(ruleDto.isActive());
    assertEquals("TestRule", ruleDto.name());
    assertEquals("stringValue", ruleDto.value());
  }

  @Test
  void testOf_WithNumericValue() {
    Rule rule = new Rule("TestRule", 42, true);
    rule.setValue(null);
    rule.setNumericValue(42);

    RuleDto ruleDto = RuleDto.of(rule);

    assertNotNull(ruleDto);
    assertTrue(ruleDto.isActive());
    assertEquals("TestRule", ruleDto.name());
    assertEquals("42", ruleDto.value());
  }

  @Test
  void testOf_WithNullValue() {
    Rule rule = new Rule("TestRule", false);
    rule.setValue(null);
    rule.setNumericValue(null);

    RuleDto ruleDto = RuleDto.of(rule);

    assertNotNull(ruleDto);
    assertFalse(ruleDto.isActive());
    assertEquals("TestRule", ruleDto.name());
    assertNull(ruleDto.value());
  }
}
