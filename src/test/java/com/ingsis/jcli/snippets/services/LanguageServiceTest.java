package com.ingsis.jcli.snippets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ingsis.jcli.snippets.clients.LanguageRestClient;
import com.ingsis.jcli.snippets.clients.factory.FeignException;
import com.ingsis.jcli.snippets.clients.factory.LanguageRestTemplateFactory;
import com.ingsis.jcli.snippets.common.exceptions.ErrorFetchingClientData;
import com.ingsis.jcli.snippets.common.exceptions.NoSuchLanguageException;
import com.ingsis.jcli.snippets.common.language.LanguageResponse;
import com.ingsis.jcli.snippets.common.language.LanguageSuccess;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.common.requests.RuleDto;
import com.ingsis.jcli.snippets.common.requests.TestCaseRequest;
import com.ingsis.jcli.snippets.common.requests.TestState;
import com.ingsis.jcli.snippets.common.requests.TestType;
import com.ingsis.jcli.snippets.common.requests.ValidateRequest;
import com.ingsis.jcli.snippets.common.responses.ErrorResponse;
import com.ingsis.jcli.snippets.common.responses.FormatResponse;
import com.ingsis.jcli.snippets.common.status.ProcessStatus;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.models.TestCase;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class LanguageServiceTest {

  @Autowired private LanguageService languageService;

  @MockBean private LanguageRestTemplateFactory languageRestTemplateFactory;

  @MockBean private LanguageRestClient languageRestClient;

  @MockBean private JwtDecoder jwtDecoder;

  private static final String languageOk = "printscript";
  private static final String versionOk = "1.1";
  private static final LanguageVersion languageVersionOk =
      new LanguageVersion(languageOk, versionOk);
  private static final String url = "${PRINTSCRIPT_URL}"; // fix "http://printscript:8080/";

  @Test
  public void getLanguageVersionOk() {
    LanguageVersion expected = new LanguageVersion(languageOk, versionOk);

    assertEquals(expected, languageService.getLanguageVersion(languageOk, versionOk));
  }

  @Test
  public void getLanguageVersionException() {
    String language = "notjava";
    String version = "8";

    NoSuchLanguageException exception =
        assertThrows(
            NoSuchLanguageException.class,
            () -> languageService.getLanguageVersion(language, version));

    assertEquals(language, exception.getLanguage());
  }

  @Test
  public void validateSnippetOk() {
    Snippet snippet = new Snippet("name", "url", "userId", languageVersionOk);
    ValidateRequest request = new ValidateRequest(snippet.getName(), snippet.getUrl(), versionOk);

    ErrorResponse response = new ErrorResponse(null);

    when(languageRestTemplateFactory.createClient(url)).thenReturn(languageRestClient);
    when(languageRestClient.validate(request)).thenReturn(response);

    assertEquals(
        new LanguageSuccess(), languageService.validateSnippet(snippet, languageVersionOk));
  }

  @Test
  public void validateSnippetException() {
    String language = "languageThatDoesNotExist";
    LanguageVersion languageVersion = new LanguageVersion(language, "123");
    Snippet snippet = new Snippet("name", "url", "userId", languageVersion);

    NoSuchLanguageException exception =
        assertThrows(
            NoSuchLanguageException.class,
            () -> languageService.validateSnippet(snippet, languageVersion));

    assertEquals(language.toLowerCase(), exception.getLanguage());
  }

  @Test
  public void getFormattingRules() throws FeignException {
    List<RuleDto> expectedRules =
        List.of(
            new RuleDto(true, "declaration_space_before_colon", null),
            new RuleDto(true, "declaration_space_after_colon", null));

    ResponseEntity<List<RuleDto>> httpResponse = new ResponseEntity<>(expectedRules, HttpStatus.OK);

    when(languageRestTemplateFactory.createClient(url)).thenReturn(languageRestClient);
    when(languageRestClient.getFormattingRules("1.1")).thenReturn(httpResponse.getBody());
    List<RuleDto> result = languageService.getFormattingRules(languageVersionOk);

    assertEquals(expectedRules, result);
  }

  @Test
  public void getLintingRules() throws FeignException {
    List<RuleDto> expectedRules =
        List.of(
            new RuleDto(true, "declaration_space_before_colon", null),
            new RuleDto(true, "declaration_space_after_colon", null));

    when(languageRestTemplateFactory.createClient(url)).thenReturn(languageRestClient);
    when(languageRestClient.getLintingRules("1.1")).thenReturn(expectedRules);
    List<RuleDto> result = languageService.getLintingRules(languageVersionOk);

    assertEquals(expectedRules, result);
  }

  @Test
  public void getLintingRulesNoSuchLanguageException() {
    String language = "unknown";
    LanguageVersion languageVersion = new LanguageVersion(language, versionOk);

    NoSuchLanguageException exception =
        assertThrows(
            NoSuchLanguageException.class, () -> languageService.getLintingRules(languageVersion));

    assertEquals(language, exception.getLanguage());
  }

  @Test
  public void getFormattingRulesNoSuchLanguageException() {
    String language = "unknown";
    LanguageVersion languageVersion = new LanguageVersion(language, versionOk);

    NoSuchLanguageException exception =
        assertThrows(
            NoSuchLanguageException.class,
            () -> languageService.getFormattingRules(languageVersion));

    assertEquals(language, exception.getLanguage());
  }

  @Test
  public void getLanguageVersionMissingUrl() {
    String language = "invalidLanguage";
    String version = "1.0";
    LanguageVersion languageVersion = new LanguageVersion(language, version);

    NoSuchLanguageException exception =
        assertThrows(
            NoSuchLanguageException.class,
            () -> languageService.getLanguageVersion(language, version));
    assertEquals(language, exception.getLanguage());
  }

  @Test
  public void validateSnippetSuccess() {
    Snippet snippet = new Snippet("name", "url", "userId", languageVersionOk);
    ValidateRequest request = new ValidateRequest(snippet.getName(), snippet.getUrl(), versionOk);
    ErrorResponse successResponse = new ErrorResponse(null);

    when(languageRestTemplateFactory.createClient(url)).thenReturn(languageRestClient);
    when(languageRestClient.validate(request)).thenReturn(successResponse);

    LanguageResponse response = languageService.validateSnippet(snippet, languageVersionOk);
    assertEquals(new LanguageSuccess(), response);
  }

  @Test
  public void testErrorFetchingClientData() {
    String errorMessage = "Internal server error";
    LanguageVersion languageVersion = new LanguageVersion(languageOk, versionOk);
    ErrorFetchingClientData exception = new ErrorFetchingClientData(errorMessage, languageVersion);
    assertEquals(
        "Error getting data from the client LanguageVersion"
            + "(language=printscript, version=1.1) : Internal server error",
        exception.getMessage());
  }

  @Test
  void runTestCaseSuccess() {
    LanguageVersion languageVersion = new LanguageVersion(languageOk, versionOk);
    Snippet snippet = new Snippet("SnippetName", "SnippetUrl", "UserId", languageVersion);
    TestCase testCase =
        new TestCase(
            snippet,
            "Test Case",
            List.of("input1"),
            List.of("output1"),
            TestType.VALID,
            TestState.PENDING);

    TestCaseRequest request =
        new TestCaseRequest(
            "SnippetName", "SnippetUrl", versionOk, List.of("input1"), List.of("output1"));

    when(languageRestTemplateFactory.createClient(url)).thenReturn(languageRestClient);
    when(languageRestClient.runTestCase(request)).thenReturn(TestType.VALID);
    TestState result = languageService.runTestCase(testCase);
    assertEquals(TestState.SUCCESS, result);
  }

  @Test
  void runTestCaseNoSuchLanguageException() {
    LanguageVersion invalidVersion = new LanguageVersion("unknownLanguage", versionOk);
    Snippet snippet = new Snippet("SnippetName", "SnippetUrl", "UserId", invalidVersion);
    TestCase testCase =
        new TestCase(
            snippet,
            "Test Case",
            List.of("input1"),
            List.of("output1"),
            TestType.VALID,
            TestState.PENDING);

    NoSuchLanguageException exception =
        assertThrows(
            NoSuchLanguageException.class,
            () -> {
              languageService.runTestCase(testCase);
            });

    assertEquals("unknownLanguage".toLowerCase(), exception.getLanguage());
  }

  @Test
  void runTestCaseClientError() {
    LanguageVersion languageVersion = new LanguageVersion(languageOk, versionOk);
    Snippet snippet = new Snippet("SnippetName", "SnippetUrl", "UserId", languageVersion);
    TestCase testCase =
        new TestCase(
            snippet,
            "Test Case",
            List.of("input1"),
            List.of("output1"),
            TestType.VALID,
            TestState.PENDING);

    TestCaseRequest request =
        new TestCaseRequest(
            "SnippetName", "SnippetUrl", versionOk, List.of("input1"), List.of("output1"));

    when(languageRestTemplateFactory.createClient(url)).thenReturn(languageRestClient);
    when(languageRestClient.runTestCase(request)).thenReturn(TestType.INVALID);

    TestState result = languageService.runTestCase(testCase);

    assertEquals(TestState.FAILURE, result);
  }

  @Test
  void formatSnippetSuccess() {
    List<RuleDto> rules = List.of(new RuleDto(true, "rule1", "value1"));
    Snippet snippet = new Snippet("SnippetName", "url", "userId", languageVersionOk);
    FormatResponse expectedResponse =
        new FormatResponse("formatted content", ProcessStatus.COMPLIANT);

    when(languageRestTemplateFactory.createClient(url)).thenReturn(languageRestClient);
    when(languageRestClient.format(any())).thenReturn(expectedResponse);

    FormatResponse response = languageService.formatSnippet(rules, snippet, languageVersionOk);

    assertEquals(expectedResponse.content(), response.content());
    assertEquals(expectedResponse.status(), response.status());
  }

  @Test
  void testGetAllExtensions() {
    assertEquals(
        languageService.getAllExtensions(), Map.of("printscript1", "ps", "printscript2", "ps"));
  }

  @Test
  void testGetPrintScriptExtension() {
    assertEquals(languageService.getExtension("printscript1"), "ps");
  }

  @Test
  void testNotValidLanguageExtension() {
    NoSuchLanguageException exception =
        assertThrows(NoSuchLanguageException.class, () -> languageService.getExtension("notvalid"));
    assertEquals("notvalid", exception.getLanguage());
  }
}
