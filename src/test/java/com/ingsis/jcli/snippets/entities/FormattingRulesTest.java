package com.ingsis.jcli.snippets.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ingsis.jcli.snippets.models.FormattingRules;
import com.ingsis.jcli.snippets.models.Rule;
import java.util.List;
import org.junit.jupiter.api.Test;

class FormattingRulesTest {

  @Test
  void testEqualsAndHashCode() {
    Rule rule1 = new Rule("rule1", "NoConsoleLog", true);
    rule1.setId(1L);
    Rule rule2 = new Rule("rule2", "NoConsoleLog", true);
    rule2.setId(2L);

    FormattingRules formattingRules1 = new FormattingRules("user123", List.of(rule1, rule2));
    FormattingRules formattingRules2 = new FormattingRules("user123", List.of(rule1, rule2));

    assertEquals(formattingRules1, formattingRules2);
    assertEquals(formattingRules1.hashCode(), formattingRules2.hashCode());

    FormattingRules formattingRules3 = new FormattingRules("user456", List.of(rule1));
    assertNotEquals(formattingRules1, formattingRules3);
  }

  @Test
  void testToString() {
    Rule rule1 = new Rule("rule1", "NoConsoleLog", true);
    rule1.setId(1L);

    FormattingRules formattingRules = new FormattingRules("user123", List.of(rule1));

    String expectedString =
        "FormattingRules(userId=user123, "
            + "rules=[Rule(id=1, name=rule1, "
            + "isActive=true, value=NoConsoleLog, numericValue=null)])";

    assertEquals(expectedString, formattingRules.toString());
  }

  @Test
  void testSetUserId() {
    FormattingRules formattingRules = new FormattingRules();
    formattingRules.setUserId("newUserId");

    assertEquals("newUserId", formattingRules.getUserId());
  }

  @Test
  void testSetRules() {
    Rule rule1 = new Rule("rule1", "NoConsoleLog", true);
    rule1.setId(1L);

    FormattingRules formattingRules = new FormattingRules();
    formattingRules.setRules(List.of(rule1));

    assertEquals(1, formattingRules.getRules().size());
    assertEquals(rule1, formattingRules.getRules().get(0));
  }

  @Test
  void testNoArgsConstructor() {
    FormattingRules formattingRules = new FormattingRules();

    assertNotNull(formattingRules);
  }

  @Test
  void testAllArgsConstructor() {
    Rule rule1 = new Rule("rule1", "NoConsoleLog", true);

    FormattingRules formattingRules = new FormattingRules("user123", List.of(rule1));

    assertEquals("user123", formattingRules.getUserId());
    assertEquals(1, formattingRules.getRules().size());
  }
}
