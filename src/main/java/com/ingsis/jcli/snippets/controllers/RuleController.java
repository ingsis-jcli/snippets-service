package com.ingsis.jcli.snippets.controllers;

import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.models.Rule;
import com.ingsis.jcli.snippets.services.JwtService;
import com.ingsis.jcli.snippets.services.LanguageService;
import com.ingsis.jcli.snippets.services.RulesService;
import com.ingsis.jcli.snippets.services.SnippetService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rules")
public class RuleController {
  private final RulesService rulesService;
  private final LanguageService languageService;
  private final SnippetService snippetService;
  private final JwtService jwtService;

  @Autowired
  public RuleController(
      RulesService rulesService,
      LanguageService languageService,
      SnippetService snippetService,
      JwtService jwtService) {
    this.rulesService = rulesService;
    this.languageService = languageService;
    this.snippetService = snippetService;
    this.jwtService = jwtService;
  }

  @GetMapping("/formatting")
  public ResponseEntity<List<Rule>> getFormattingRules(
      @RequestParam(value = "language", defaultValue = "printscript") String language,
      @RequestParam(value = "version", defaultValue = "1.1") String version,
      @RequestHeader("Authorization") String token) {

    String userId = jwtService.extractUserId(token);

    LanguageVersion languageVersion = languageService.getLanguageVersion(language, version);
    List<Rule> rules = rulesService.getFormattingRules(userId, languageVersion);

    return new ResponseEntity<>(rules, HttpStatus.OK);
  }

  @GetMapping("/linting")
  public ResponseEntity<List<Rule>> getLintingRules(
      @RequestParam(value = "language", defaultValue = "printscript") String language,
      @RequestParam(value = "version", defaultValue = "1.1") String version,
      @RequestHeader("Authorization") String token) {

    String userId = jwtService.extractUserId(token);

    LanguageVersion languageVersion = languageService.getLanguageVersion(language, version);
    List<Rule> rules = rulesService.getLintingRules(userId, languageVersion);

    return new ResponseEntity<>(rules, HttpStatus.OK);
  }

  @PutMapping("/formatting")
  public ResponseEntity<Void> updateFormattingRules(
      @RequestBody @Valid List<Rule> rules,
      @RequestParam(value = "language", defaultValue = "printscript") String language,
      @RequestParam(value = "version", defaultValue = "1.1") String version,
      @RequestHeader("Authorization") String token) {

    String userId = jwtService.extractUserId(token);

    rulesService.updateFormattingRules(userId, rules);
    LanguageVersion languageVersion = languageService.getLanguageVersion(language, version);
    snippetService.formatUserSnippets(userId, languageVersion);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping("/linting")
  public ResponseEntity<Void> updateLintingRules(
      @RequestBody @Valid List<Rule> rules,
      @RequestParam(value = "language", defaultValue = "printscript") String language,
      @RequestParam(value = "version", defaultValue = "1.1") String version,
      @RequestHeader("Authorization") String token) {

    String userId = jwtService.extractUserId(token);

    rulesService.updateLintingRules(userId, rules);
    LanguageVersion languageVersion = languageService.getLanguageVersion(language, version);
    snippetService.lintUserSnippets(userId, languageVersion);

    return new ResponseEntity<>(HttpStatus.OK);
  }
}
