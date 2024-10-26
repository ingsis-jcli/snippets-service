package com.ingsis.jcli.snippets.entities;

import com.ingsis.jcli.snippets.models.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RuleTests {

  @Test
  void testConstructorWithNumberValue() {
    Number value = 42;
    Rule rule = new Rule("ruleWithNumber", true, value);
    Assertions.assertEquals("ruleWithNumber", rule.getName());
    Assertions.assertTrue(rule.isActive());
    Assertions.assertEquals(value.toString(), rule.getValue());

    Assertions.assertEquals(value.doubleValue(), rule.getNumericValue().doubleValue(), 0.0);
  }

  @Test
  void testGetNumericValue() {
    Rule ruleWithNumber = new Rule("ruleWithNumber", true, 42);
    Rule ruleWithNonNumber = new Rule("ruleWithNonNumber", true, "NoConsoleLog");

    Assertions.assertEquals(42.0, ruleWithNumber.getNumericValue().doubleValue(), 0.0);
    Assertions.assertNull(ruleWithNonNumber.getNumericValue());
  }

  @Test
  void testEqualsAndHashCode() {
    Rule rule1 = new Rule("rule1", true, "NoConsoleLog");
    rule1.setId(1L);
    Rule rule2 = new Rule("rule1", true, "NoConsoleLog");
    rule2.setId(1L);
    Rule rule3 = new Rule("rule3", false, "NoConsoleLog");
    rule3.setId(2L);

    Assertions.assertEquals(rule1, rule2);
    Assertions.assertNotEquals(rule1, rule3);
    Assertions.assertEquals(rule1.hashCode(), rule2.hashCode());
  }

  @Test
  void testToString() {
    Rule rule = new Rule("rule1", true, "NoConsoleLog");
    rule.setId(1L);
    String expectedString = "Rule(id=1, name=rule1, isActive=true, value=NoConsoleLog)";
    Assertions.assertEquals(expectedString, rule.toString());
  }

  @Test
  void testGettersAndSetters() {
    Rule rule = new Rule();
    rule.setId(1L);
    rule.setName("rule1");
    rule.setActive(true);
    rule.setValue("NoConsoleLog");

    Assertions.assertEquals(1L, rule.getId());
    Assertions.assertEquals("rule1", rule.getName());
    Assertions.assertTrue(rule.isActive());
    Assertions.assertEquals("NoConsoleLog", rule.getValue());
  }

  @Test
  void testConstructorWithStringValue() {
    String value = "NoConsoleLog";
    Rule rule = new Rule("ruleWithString", true, value);
    Assertions.assertEquals("ruleWithString", rule.getName());
    Assertions.assertTrue(rule.isActive());
    Assertions.assertEquals(value, rule.getValue());
  }

  @Test
  void testDefaultConstructor() {
    Rule rule = new Rule();
    Assertions.assertNotNull(rule);
  }
}
