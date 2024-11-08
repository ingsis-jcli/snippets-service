package com.ingsis.jcli.snippets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.common.requests.RuleDto;
import com.ingsis.jcli.snippets.models.FormattingRules;
import com.ingsis.jcli.snippets.models.LintingRules;
import com.ingsis.jcli.snippets.models.Rule;
import com.ingsis.jcli.snippets.producers.FormatSnippetsProducer;
import com.ingsis.jcli.snippets.producers.LintSnippetsProducer;
import com.ingsis.jcli.snippets.repositories.FormattingRulesRepository;
import com.ingsis.jcli.snippets.repositories.LintingRulesRepository;
import com.ingsis.jcli.snippets.repositories.RuleRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RulesServiceTest {

  @Mock private FormattingRulesRepository formattingRulesRepository;

  @Mock private LintingRulesRepository lintingRulesRepository;

  @Mock private RuleRepository ruleRepository;

  @Mock private LanguageService languageService;

  @Mock private LintSnippetsProducer lintSnippetsProducer;

  @Mock private FormatSnippetsProducer formatSnippetsProducer;

  @InjectMocks private RulesService rulesService;

  private static final String USER_ID = "123";
  private static final LanguageVersion LANGUAGE_VERSION = new LanguageVersion("printscript", "1.1");

  @Test
  void testUpdateLintingRules() {
    List<Rule> rules = List.of(new Rule("rule1", 10, true));
    LintingRules lintingRules = new LintingRules(USER_ID, rules);

    when(lintingRulesRepository.findByUserId(USER_ID)).thenReturn(Optional.of(lintingRules));

    rulesService.updateLintingRules(USER_ID, rules);

    verify(lintingRulesRepository).save(lintingRules);
  }

  @Test
  void testUpdateFormattingRules() {
    List<Rule> rules = List.of(new Rule("formatRule", "value1", true));
    FormattingRules formattingRules = new FormattingRules(USER_ID, rules);

    when(formattingRulesRepository.findAllByUserId(USER_ID))
        .thenReturn(Optional.of(formattingRules));

    rulesService.updateFormattingRules(USER_ID, rules);

    verify(formattingRulesRepository).save(formattingRules);
  }

  @Test
  void testGetLintingRules() {
    List<RuleDto> defaultRules = List.of(new RuleDto(true, "rule1", null));
    when(languageService.getLintingRules(LANGUAGE_VERSION)).thenReturn(defaultRules);

    List<Rule> rules = rulesService.getLintingRules(USER_ID, LANGUAGE_VERSION);

    assertEquals(1, rules.size());
    assertEquals("rule1", rules.get(0).getName());
    assertTrue(rules.get(0).isActive());

    verify(ruleRepository).saveAll(any(List.class));
    verify(lintingRulesRepository).save(any(LintingRules.class));
  }

  @Test
  void testGetFormattingRules() {
    List<RuleDto> defaultRules = List.of(new RuleDto(true, "formatRule", "10"));
    when(languageService.getFormattingRules(LANGUAGE_VERSION)).thenReturn(defaultRules);

    List<Rule> rules = rulesService.getFormattingRules(USER_ID, LANGUAGE_VERSION);

    assertEquals(1, rules.size());
    assertEquals("formatRule", rules.get(0).getName());
    assertEquals(10, rules.get(0).getNumericValue());
    assertTrue(rules.get(0).isActive());

    verify(ruleRepository).saveAll(any(List.class));
    verify(formattingRulesRepository).save(any(FormattingRules.class));
  }
}
