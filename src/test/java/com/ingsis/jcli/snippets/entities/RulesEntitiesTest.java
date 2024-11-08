package com.ingsis.jcli.snippets.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ingsis.jcli.snippets.models.FormattingRules;
import com.ingsis.jcli.snippets.models.LintingRules;
import com.ingsis.jcli.snippets.models.Rule;
import com.ingsis.jcli.snippets.repositories.FormattingRulesRepository;
import com.ingsis.jcli.snippets.repositories.LintingRulesRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RulesEntitiesTest {

  @Mock private FormattingRulesRepository formattingRulesRepository;

  @Mock private LintingRulesRepository lintingRulesRepository;

  private FormattingRules formattingRules;
  private LintingRules lintingRules;
  private Rule rule1;
  private Rule rule2;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    rule1 = new Rule();
    rule1.setId(2L);
    rule1.setActive(true);
    rule1.setName("NoConsoleLog");

    rule2 = new Rule();
    rule2.setId(1L);
    rule2.setName("MaxLineLength");
    rule2.setActive(true);
    rule2.setValue("80");

    formattingRules = new FormattingRules("user123", List.of(rule1, rule2));
    lintingRules = new LintingRules("user456", List.of(rule1));
  }

  @Test
  void testSaveAndFindFormattingRules() {
    when(formattingRulesRepository.save(any(FormattingRules.class))).thenReturn(formattingRules);
    when(formattingRulesRepository.findAllByUserId("user123"))
        .thenReturn(Optional.of(formattingRules));

    FormattingRules savedFormattingRules = formattingRulesRepository.save(formattingRules);

    assertEquals("user123", savedFormattingRules.getUserId());
    assertEquals(2, savedFormattingRules.getRules().size());
    assertEquals("NoConsoleLog", savedFormattingRules.getRules().get(0).getName());

    Optional<FormattingRules> retrievedFormattingRules =
        formattingRulesRepository.findAllByUserId("user123");
    assertTrue(retrievedFormattingRules.isPresent());
    assertEquals("user123", retrievedFormattingRules.get().getUserId());
    assertEquals(2, retrievedFormattingRules.get().getRules().size());
  }

  @Test
  void testSaveAndFindLintingRules() {
    when(lintingRulesRepository.save(any(LintingRules.class))).thenReturn(lintingRules);
    when(lintingRulesRepository.findByUserId("user456")).thenReturn(Optional.of(lintingRules));
    LintingRules savedLintingRules = lintingRulesRepository.save(lintingRules);
    assertEquals("user456", savedLintingRules.getUserId());
    assertEquals(1, savedLintingRules.getRules().size());
    assertEquals("NoConsoleLog", savedLintingRules.getRules().get(0).getName());

    Optional<LintingRules> retrievedLintingRules = lintingRulesRepository.findByUserId("user456");
    assertTrue(retrievedLintingRules.isPresent());
    assertEquals("user456", retrievedLintingRules.get().getUserId());
    assertEquals(1, retrievedLintingRules.get().getRules().size());
  }

  @Test
  void testUpdateFormattingRules() {
    when(formattingRulesRepository.save(any(FormattingRules.class))).thenReturn(formattingRules);
    when(formattingRulesRepository.findAllByUserId("user123"))
        .thenReturn(Optional.of(formattingRules));
    rule2.setActive(false);
    formattingRules.setRules(List.of(rule1, rule2));
    FormattingRules updatedFormattingRules = formattingRulesRepository.save(formattingRules);
    assertEquals(2, updatedFormattingRules.getRules().size());
    assertEquals(false, updatedFormattingRules.getRules().get(1).isActive());
  }
}
