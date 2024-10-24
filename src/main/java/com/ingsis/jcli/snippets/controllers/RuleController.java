package com.ingsis.jcli.snippets.controllers;

import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.dto.RulesDto;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import com.ingsis.jcli.snippets.dto.UpdateRulesDto;
import com.ingsis.jcli.snippets.models.FormattingRules;
import com.ingsis.jcli.snippets.models.LintingRules;
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
import org.springframework.web.bind.annotation.*;

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
  public ResponseEntity<List<Rule>> getFormattingRules(@RequestBody @Valid RulesDto rulesDto) {
    FormattingRules rules =
        rulesService.getFormattingRules(
            rulesDto.getUserId(), rulesDto.getLanguage(), rulesDto.getVersion());
    List<Rule> ruleList = rules.getRules();
    return new ResponseEntity<>(ruleList, HttpStatus.OK);
  }

  @GetMapping("/linting")
  public ResponseEntity<List<Rule>> getLintingRules(@RequestBody @Valid RulesDto rulesDto) {
    LintingRules rules =
        rulesService.getLintingRules(
            rulesDto.getUserId(), rulesDto.getLanguage(), rulesDto.getVersion());
    List<Rule> ruleList = rules.getRules();
    return new ResponseEntity<>(ruleList, HttpStatus.OK);
  }

  @PutMapping("/formatting")
  public ResponseEntity<Void> updateFormattingRules(@RequestBody @Valid UpdateRulesDto rulesDto) {
    rulesService.updateFormattingRules(rulesDto.getUserId(), rulesDto.getRules());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping("/linting")
  public ResponseEntity<Void> updateLintingRules(
      @RequestBody @Valid UpdateRulesDto rulesDto,
      @RequestParam(value = "language", defaultValue = "printscript") String language,
      @RequestParam(value = "version", defaultValue = "1.1") String version,
      @RequestHeader("Authorization") String token) {

    String userId = jwtService.extractUserId(token);

    rulesService.updateLintingRules(rulesDto.getUserId(), rulesDto.getRules());
    LanguageVersion languageVersion = languageService.getLanguageVersion(language, version);
    snippetService.lintUsersSnippets(userId, languageVersion);

    return new ResponseEntity<>(HttpStatus.OK);
  }
}
