package com.ingsis.jcli.snippets.services;

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
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RulesService {
  final FormattingRulesRepository formattingRulesRepository;
  final LintingRulesRepository lintingRulesRepository;
  final RuleRepository ruleRepository;
  final LanguageService languageService;
  final LintSnippetsProducer lintSnippetsProducer;
  final FormatSnippetsProducer formatSnippetsProducer;

  @Autowired
  public RulesService(
      FormattingRulesRepository formattingRulesRepository,
      LintingRulesRepository lintingRulesRepository,
      LanguageService languageService,
      RuleRepository ruleRepository,
      LintSnippetsProducer lintSnippetsProducer,
      FormatSnippetsProducer formatSnippetsProducer) {
    this.formattingRulesRepository = formattingRulesRepository;
    this.lintingRulesRepository = lintingRulesRepository;
    this.languageService = languageService;
    this.ruleRepository = ruleRepository;
    this.lintSnippetsProducer = lintSnippetsProducer;
    this.formatSnippetsProducer = formatSnippetsProducer;
  }

  public void updateLintingRules(String userId, List<Rule> rules) {
    Optional<LintingRules> lintingRules = lintingRulesRepository.findByUserId(userId);
    if (lintingRules.isPresent()) {
      lintingRules.get().setRules(rules);
      lintingRulesRepository.save(lintingRules.get());
    } else {
      LintingRules newLintingRules = new LintingRules(userId, rules);
      lintingRulesRepository.save(newLintingRules);
    }
  }

  public void updateFormattingRules(String userId, List<Rule> rules) {
    Optional<FormattingRules> formattingRules = formattingRulesRepository.findAllByUserId(userId);
    if (formattingRules.isPresent()) {
      formattingRules.get().setRules(rules);
      formattingRulesRepository.save(formattingRules.get());
    } else {
      FormattingRules newFormattingRules = new FormattingRules(userId, rules);
      formattingRulesRepository.save(newFormattingRules);
    }
  }

  public List<Rule> getLintingRules(String userId, LanguageVersion languageVersion) {
    Optional<LintingRules> rules = lintingRulesRepository.findByUserId(userId);
    if (rules.isPresent()) {
      return rules.get().getRules();
    }

    List<RuleDto> defaultRules = languageService.getLintingRules(languageVersion);
    List<Rule> ruleEntities =
        defaultRules.stream()
            .map(
                ruleDto -> {
                  try {
                    Integer numericValue = Integer.parseInt(ruleDto.value());
                    return new Rule(ruleDto.name(), numericValue, ruleDto.isActive());
                  } catch (NumberFormatException e) {
                    if (ruleDto.value() == null) {
                      return new Rule(ruleDto.name(), ruleDto.isActive());
                    }
                    return new Rule(ruleDto.name(), ruleDto.value(), ruleDto.isActive());
                  }
                })
            .collect(Collectors.toList());

    ruleRepository.saveAll(ruleEntities);

    LintingRules lintingRules = new LintingRules(userId, ruleEntities);
    lintingRulesRepository.save(lintingRules);

    return lintingRules.getRules();
  }

  public List<Rule> getFormattingRules(String userId, LanguageVersion languageVersion) {
    Optional<FormattingRules> rules = formattingRulesRepository.findAllByUserId(userId);
    if (rules.isPresent()) {
      return rules.get().getRules();
    }

    List<RuleDto> defaultRules = languageService.getFormattingRules(languageVersion);
    List<Rule> ruleEntities =
        defaultRules.stream()
            .map(
                ruleDto -> {
                  try {
                    Integer numericValue = Integer.parseInt(ruleDto.value());
                    return new Rule(ruleDto.name(), numericValue, ruleDto.isActive());
                  } catch (NumberFormatException e) {
                    return new Rule(ruleDto.name(), ruleDto.isActive());
                  }
                })
            .collect(Collectors.toList());

    ruleRepository.saveAll(ruleEntities);

    FormattingRules formattingRules = new FormattingRules(userId, ruleEntities);
    formattingRulesRepository.save(formattingRules);

    return formattingRules.getRules();
  }
}
