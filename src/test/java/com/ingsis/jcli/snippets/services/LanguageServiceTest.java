package com.ingsis.jcli.snippets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import com.ingsis.jcli.snippets.clients.LanguageClient;
import com.ingsis.jcli.snippets.clients.LanguageRestClient;
import com.ingsis.jcli.snippets.clients.LanguageRestTemplateFactory;
import com.ingsis.jcli.snippets.clients.factory.FeignException;
import com.ingsis.jcli.snippets.clients.factory.LanguageClientFactory;
import com.ingsis.jcli.snippets.common.exceptions.ErrorFetchingClientData;
import com.ingsis.jcli.snippets.common.exceptions.NoSuchLanguageException;
import com.ingsis.jcli.snippets.common.language.LanguageResponse;
import com.ingsis.jcli.snippets.common.language.LanguageSuccess;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.common.requests.ValidateRequest;
import com.ingsis.jcli.snippets.common.responses.DefaultRule;
import com.ingsis.jcli.snippets.common.responses.ErrorResponse;
import java.util.List;
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

  @MockBean private LanguageClientFactory languageClientFactory;

  @MockBean private LanguageRestTemplateFactory languageRestTemplateFactory;

  @MockBean private LanguageClient languageClient;

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
    String language = "java";
    String version = "8";

    NoSuchLanguageException exception =
        assertThrows(
            NoSuchLanguageException.class,
            () -> languageService.getLanguageVersion(language, version));

    assertEquals(language, exception.getLanguage());
  }

  @Test
  public void validateSnippetOk() {
    String snippet = "content";
    ValidateRequest request = new ValidateRequest(snippet, versionOk);
    ErrorResponse response = new ErrorResponse("");
    ResponseEntity<ErrorResponse> httpResponse = new ResponseEntity<>(response, HttpStatus.OK);

    when(languageClientFactory.createClient(url)).thenReturn(languageClient);
    try {
      when(languageClient.validate(request)).thenReturn(httpResponse);
    } catch (FeignException e) {
      throw new RuntimeException(e);
    }

    assertEquals(
        new LanguageSuccess(), languageService.validateSnippet(snippet, languageVersionOk));
  }

  @Test
  public void validateSnippetException() {
    String snippet = "content";
    String language = "lua";
    LanguageVersion languageVersion = new LanguageVersion(language, "123");

    NoSuchLanguageException exception =
        assertThrows(
            NoSuchLanguageException.class,
            () -> languageService.validateSnippet(snippet, languageVersion));

    assertEquals(language, exception.getLanguage());
  }

  @Test
  public void getFormattingRules() throws FeignException {
    List<DefaultRule> expectedRules =
        List.of(
            new DefaultRule("declaration_space_before_colon", true, null),
            new DefaultRule("declaration_space_after_colon", true, null));
    ResponseEntity<List<DefaultRule>> httpResponse =
        new ResponseEntity<>(expectedRules, HttpStatus.OK);
    when(languageClientFactory.createClient(url)).thenReturn(languageClient);
    when(languageClient.getFormattingRules("1.1")).thenReturn(httpResponse);
    List<DefaultRule> result = languageService.getFormattingRules(languageVersionOk);
    assertEquals(expectedRules, result);
  }

  @Test
  public void getLintingRules() throws FeignException {
    List<DefaultRule> expectedRules =
        List.of(
            new DefaultRule("declaration_space_before_colon", true, null),
            new DefaultRule("declaration_space_after_colon", true, null));
    when(languageRestTemplateFactory.createClient(url)).thenReturn(languageRestClient);
    when(languageRestClient.getLintingRules("1.1")).thenReturn(expectedRules);
    List<DefaultRule> result = languageService.getLintingRules(languageVersionOk);
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
  public void getFormattingRulesFeignException() throws FeignException {
    when(languageClientFactory.createClient(url)).thenReturn(languageClient);
    JsonObject errorPayload = new JsonObject();
    errorPayload.addProperty("error", "Internal server error");
    ResponseEntity<JsonObject> errorResponseEntity =
        new ResponseEntity<>(errorPayload, HttpStatus.INTERNAL_SERVER_ERROR);
    FeignException feignException = new FeignException(errorResponseEntity);
    when(languageClient.getFormattingRules("1.1")).thenThrow(feignException);
    ErrorFetchingClientData exception =
        assertThrows(
            ErrorFetchingClientData.class,
            () -> languageService.getFormattingRules(languageVersionOk));
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
  public void validateSnippetSuccess() throws FeignException {
    String snippet = "valid content";
    ValidateRequest request = new ValidateRequest(snippet, versionOk);
    ErrorResponse successResponse = new ErrorResponse("");
    ResponseEntity<ErrorResponse> httpResponse =
        new ResponseEntity<>(successResponse, HttpStatus.OK);

    when(languageClientFactory.createClient(url)).thenReturn(languageClient);
    when(languageClient.validate(request)).thenReturn(httpResponse);

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
  public void getResponseNon2xx() {
    String errorMessage = "Bad Request";
    ErrorResponse errorResponse = new ErrorResponse(errorMessage);
    ResponseEntity<ErrorResponse> responseEntity =
        new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    LanguageResponse response = languageService.getResponse(responseEntity);
    assertEquals(errorMessage, response.getError());
  }
}
