package com.ingsis.jcli.snippets.controllers;

import com.ingsis.jcli.snippets.common.requests.TestState;
import com.ingsis.jcli.snippets.dto.TestCaseDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.models.TestCase;
import com.ingsis.jcli.snippets.services.JwtService;
import com.ingsis.jcli.snippets.services.LanguageService;
import com.ingsis.jcli.snippets.services.SnippetService;
import com.ingsis.jcli.snippets.services.TestCaseService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testcase")
public class TestsController {

  private final TestCaseService testCaseService;
  private final SnippetService snippetService;
  private final JwtService jwtService;
  private final LanguageService languageService;

  @Autowired
  public TestsController(
      TestCaseService testCaseService,
      JwtService jwtService,
      SnippetService snippetService,
      LanguageService languageService) {
    this.testCaseService = testCaseService;
    this.jwtService = jwtService;
    this.snippetService = snippetService;
    this.languageService = languageService;
  }

  @PostMapping()
  public ResponseEntity<TestCase> createTestCase(
      @RequestBody @Valid TestCaseDto testCaseDto,
      @RequestHeader(name = "Authorization") String token) {

    String userId = jwtService.extractUserId(token);

    Optional<Snippet> snippetOpt = snippetService.getSnippet(testCaseDto.snippetId());

    if (snippetOpt.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    Snippet snippet = snippetOpt.get();

    if (!snippetService.isOwner(snippet, userId)) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    TestCase testCase = testCaseService.createTestCase(testCaseDto, snippet);
    System.out.println(
        "Created a snippet test case: "
            + testCase.getName()
            + testCase.getType()
            + " with inputs "
            + testCase.getInputs()
            + " and outputs "
            + testCase.getOutputs());
    return new ResponseEntity<>(testCase, HttpStatus.CREATED);
  }

  @GetMapping("/run/{id}")
  public ResponseEntity<TestState> runTestCase(
      @PathVariable Long id, @RequestHeader(name = "Authorization") String token) {
    String userId = jwtService.extractUserId(token);

    Optional<TestCase> testCaseOp = testCaseService.getTestCase(id);

    if (testCaseOp.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    TestCase testCase = testCaseOp.get();

    Snippet snippet = testCase.getSnippet();

    if (!snippetService.isOwner(snippet, userId)) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    TestState testCaseResult = languageService.runTestCase(testCase);

    testCaseService.updateTestCaseState(testCase, testCaseResult);

    System.out.println(
        "Running a snippet test case: "
            + testCase.getName()
            + " with inputs "
            + testCase.getInputs()
            + " and outputs "
            + testCase.getOutputs()
            + " result was "
            + testCaseResult);

    return new ResponseEntity<>(testCaseResult, HttpStatus.OK);
  }

  @GetMapping()
  public List<TestCase> getTestCaseByUser(@RequestHeader(name = "Authorization") String token) {
    String userId = jwtService.extractUserId(token);
    return testCaseService.getTestCaseByUser(userId);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TestCase> getTestCase(
      @PathVariable Long id, @RequestHeader(name = "Authorization") String token) {
    String userId = jwtService.extractUserId(token);
    Optional<TestCase> testCaseOp = testCaseService.getTestCase(id);
    if (testCaseOp.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    TestCase testCase = testCaseOp.get();
    if (testCase.getSnippet().getOwner().equals(userId)) {
      System.out.println(
          "Getting a snippet test case: "
              + testCase.getName()
              + " with inputs "
              + testCase.getInputs()
              + " and outputs "
              + testCase.getOutputs());
      return ResponseEntity.ok(testCase);
    }
    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
  }

  @DeleteMapping("/{id}")
  public void deleteTestCase(
      @PathVariable Long id, @RequestHeader(name = "Authorization") String token) {
    String userId = jwtService.extractUserId(token);
    Optional<TestCase> testCaseOp = testCaseService.getTestCase(id);

    if (testCaseOp.isEmpty()) {
      return;
    }
    TestCase testCase = testCaseOp.get();

    Snippet snippet = testCase.getSnippet();

    if (!snippetService.isOwner(snippet, userId)) {
      return;
    }

    testCaseService.deleteTestCase(testCase);
  }
}
