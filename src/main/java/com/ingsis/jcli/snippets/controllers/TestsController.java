package com.ingsis.jcli.snippets.controllers;

import com.ingsis.jcli.snippets.common.PermissionType;
import com.ingsis.jcli.snippets.common.requests.TestState;
import com.ingsis.jcli.snippets.dto.TestCaseDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.models.TestCase;
import com.ingsis.jcli.snippets.services.JwtService;
import com.ingsis.jcli.snippets.services.LanguageService;
import com.ingsis.jcli.snippets.services.PermissionService;
import com.ingsis.jcli.snippets.services.SnippetService;
import com.ingsis.jcli.snippets.services.TestCaseService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-case")
public class TestsController {

  private final TestCaseService testCaseService;
  private final SnippetService snippetService;
  private final JwtService jwtService;
  private final PermissionService permissionService;
  private final LanguageService languageService;

  @Autowired
  public TestsController(
      TestCaseService testCaseService,
      JwtService jwtService,
      SnippetService snippetService,
      PermissionService permissionService,
      LanguageService languageService) {
    this.testCaseService = testCaseService;
    this.jwtService = jwtService;
    this.snippetService = snippetService;
    this.permissionService = permissionService;
    this.languageService = languageService;
  }

  @PostMapping()
  public ResponseEntity<Long> createTestCase(
      @RequestBody @Valid TestCaseDto testCaseDto,
      @RequestHeader(name = "Authorization") String token) {

    String userId = jwtService.extractUserId(token);

    boolean hasPermission =
        permissionService.hasPermissionOnSnippet(
            PermissionType.WRITE, testCaseDto.snippetId(), userId);
    // TODO: WHAT PERMISSION SHOULD I CHECK?

    if (!hasPermission) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    Optional<Snippet> snippet = snippetService.getSnippet(testCaseDto.snippetId());

    if (snippet.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    Long id = testCaseService.createTestCase(testCaseDto, snippet.get());
    return new ResponseEntity<>(id, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TestState> runTestCase(
      @PathVariable Long id, @RequestHeader(name = "Authorization") String token) {
    String userId = jwtService.extractUserId(token);

    Optional<TestCase> testCaseOp = testCaseService.getTestCase(id);

    if (testCaseOp.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    TestCase testCase = testCaseOp.get();

    Long snippetId = testCase.getSnippet().getId();

    boolean hasPermission =
        permissionService.hasPermissionOnSnippet(PermissionType.EXECUTE, snippetId, userId);

    if (!hasPermission) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    TestState testCaseResult = languageService.runTestCase(testCase);

    testCaseService.updateTestCaseState(testCase, testCaseResult);

    return new ResponseEntity<>(testCaseResult, HttpStatus.OK);
  }
}
