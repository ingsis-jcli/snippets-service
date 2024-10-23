package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.common.responses.DefaultRules;
import com.ingsis.jcli.snippets.dto.SnippetDto;
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
  final SnippetService snippetService;
  final LintSnippetsProducer lintSnippetsProducer;
  final FormatSnippetsProducer formatSnippetsProducer;

  @Autowired
  public RulesService(
      FormattingRulesRepository formattingRulesRepository,
      LintingRulesRepository lintingRulesRepository,
      LanguageService languageService,
      RuleRepository ruleRepository,
      SnippetService snippetService,
      LintSnippetsProducer lintSnippetsProducer,
      FormatSnippetsProducer formatSnippetsProducer) {
    this.formattingRulesRepository = formattingRulesRepository;
    this.lintingRulesRepository = lintingRulesRepository;
    this.languageService = languageService;
    this.ruleRepository = ruleRepository;
    this.snippetService = snippetService;
    this.lintSnippetsProducer = lintSnippetsProducer;
    this.formatSnippetsProducer = formatSnippetsProducer;
  }

  public void updateLintingRules(String userId, List<Rule> rules) {
    // Update the rules for the user
    Optional<LintingRules> lintingRules = lintingRulesRepository.findByUserId(userId);
    if (lintingRules.isPresent()) {
      lintingRules.get().setRules(rules);
      lintingRulesRepository.save(lintingRules.get());
    } else {
      LintingRules newLintingRules = new LintingRules(userId, rules);
      lintingRulesRepository.save(newLintingRules);
    }
    // Request all snippets to be linted
    List<SnippetDto> snippets = snippetService.getAllSnippets(userId);
    for (SnippetDto snippetDto : snippets) {
      lintSnippetsProducer.lint(snippetDto, rules);
    }
  }

  public void updateFormattingRules(String userId, List<Rule> rules) {
    // Update the rules for the user
    Optional<FormattingRules> formattingRules = formattingRulesRepository.findAllByUserId(userId);
    if (formattingRules.isPresent()) {
      formattingRules.get().setRules(rules);
      formattingRulesRepository.save(formattingRules.get());
    } else {
      FormattingRules newFormattingRules = new FormattingRules(userId, rules);
      formattingRulesRepository.save(newFormattingRules);
    }
    // Request all snippets to be formatted
    List<SnippetDto> snippets = snippetService.getAllSnippets(userId);
    for (SnippetDto snippetDto : snippets) {
      formatSnippetsProducer.format(snippetDto, rules);
    }
  }

  public LintingRules getLintingRules(String userId, String language, String version) {
    Optional<LintingRules> rules = lintingRulesRepository.findByUserId(userId);
    if (rules.isPresent()) {
      return rules.get();
    } else {
      LanguageVersion languageVersion = languageService.getLanguageVersion(language, version);
      DefaultRules defaultRules = languageService.getLintingRules(languageVersion);
      List<Rule> ruleEntities =
          defaultRules.rules().stream()
              .map(ruleDto -> new Rule(ruleDto.getName(), ruleDto.isActive(), ruleDto.getValue()))
              .collect(Collectors.toList());
      LintingRules lintingRules = new LintingRules(userId, ruleEntities);
      lintingRulesRepository.save(lintingRules);
      return lintingRules;
    }
  }

  public FormattingRules getFormattingRules(String userId, String language, String version) {
    Optional<FormattingRules> rules = formattingRulesRepository.findAllByUserId(userId);
    if (rules.isPresent()) {
      return rules.get();
    } else {
      LanguageVersion languageVersion = languageService.getLanguageVersion(language, version);
      DefaultRules defaultRules = languageService.getFormattingRules(languageVersion);
      List<Rule> ruleEntities =
          defaultRules.rules().stream()
              .map(ruleDto -> new Rule(ruleDto.getName(), ruleDto.isActive(), ruleDto.getValue()))
              .collect(Collectors.toList());
      FormattingRules formattingRules = new FormattingRules(userId, ruleEntities);
      formattingRulesRepository.save(formattingRules);
      return formattingRules;
    }
  }
}