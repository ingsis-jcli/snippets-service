package com.ingsis.jcli.snippets.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ingsis.jcli.snippets.models.LintingRules;
import com.ingsis.jcli.snippets.models.Rule;
import java.util.List;
import org.junit.jupiter.api.Test;

class LintingRulesTest {

  @Test
  void testEqualsAndHashCode() {
    Rule rule1 = new Rule("rule1", true, "NoConsoleLog");
    rule1.setId(1L);
    Rule rule2 = new Rule("rule2", true, "NoConsoleLog");
    rule2.setId(2L);

    LintingRules lintingRules1 = new LintingRules("user123", List.of(rule1, rule2));
    LintingRules lintingRules2 = new LintingRules("user123", List.of(rule1, rule2));

    assertEquals(lintingRules1, lintingRules2);
    assertEquals(lintingRules1.hashCode(), lintingRules2.hashCode());

    LintingRules lintingRules3 = new LintingRules("user456", List.of(rule1));
    assertNotEquals(lintingRules1, lintingRules3);
  }

  @Test
  void testToString() {
    Rule rule1 = new Rule("rule1", true, "NoConsoleLog");
    rule1.setId(1L);
    LintingRules lintingRules = new LintingRules("user123", List.of(rule1));
    String expectedString =
        "LintingRules(userId=user123, rules="
            + "[Rule(id=1, name=rule1, isActive=true, value=NoConsoleLog)])";
    assertEquals(expectedString, lintingRules.toString());
  }

  @Test
  void testSetUserId() {
    LintingRules lintingRules = new LintingRules();
    lintingRules.setUserId("newUserId");

    assertEquals("newUserId", lintingRules.getUserId());
  }

  @Test
  void testSetRules() {
    Rule rule1 = new Rule("rule1", true, "NoConsoleLog");
    rule1.setId(1L);
    LintingRules lintingRules = new LintingRules();
    lintingRules.setRules(List.of(rule1));

    assertEquals(1, lintingRules.getRules().size());
    assertEquals(rule1, lintingRules.getRules().get(0));
  }

  @Test
  void testNoArgsConstructor() {
    LintingRules lintingRules = new LintingRules();
    assertNotNull(lintingRules);
  }

  @Test
  void testAllArgsConstructor() {
    Rule rule = new Rule("rule1", true, "NoConsoleLog");
    LintingRules lintingRules = new LintingRules("user123", List.of(rule));

    assertEquals("user123", lintingRules.getUserId());
    assertEquals(1, lintingRules.getRules().size());
  }
}
