package com.ingsis.jcli.snippets.controllers;

import com.ingsis.jcli.snippets.dto.RulesDto;
import com.ingsis.jcli.snippets.dto.UpdateRulesDto;
import com.ingsis.jcli.snippets.models.FormattingRules;
import com.ingsis.jcli.snippets.models.LintingRules;
import com.ingsis.jcli.snippets.models.Rule;
import com.ingsis.jcli.snippets.services.RulesService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rules")
public class RuleController {
  final RulesService rulesService;

  @Autowired
  public RuleController(RulesService rulesService) {
    this.rulesService = rulesService;
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
  public ResponseEntity<Void> updateLintingRules(@RequestBody @Valid UpdateRulesDto rulesDto) {
    rulesService.updateLintingRules(rulesDto.getUserId(), rulesDto.getRules());
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
