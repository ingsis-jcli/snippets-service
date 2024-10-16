package com.ingsis.jcli.snippets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.ingsis.jcli.snippets.clients.LanguageClient;
import com.ingsis.jcli.snippets.clients.factory.LanguageClientFactory;
import com.ingsis.jcli.snippets.common.exceptions.NoSuchLanguageException;
import com.ingsis.jcli.snippets.common.language.LanguageResponse;
import com.ingsis.jcli.snippets.common.language.LanguageSuccess;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.common.requests.ValidateRequest;
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

  @MockBean private LanguageClient languageClient;

  @MockBean private JwtDecoder jwtDecoder;

  private static final String languageOk = "printscript";
  private static final String versionOk = "1.1";
  private static final LanguageVersion languageVersionOk =
      new LanguageVersion(languageOk, versionOk);
  private static final String url = "http://printscript:8080/";

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
    LanguageResponse response = new LanguageSuccess();
    ResponseEntity<LanguageResponse> httpResponse = new ResponseEntity<>(response, HttpStatus.OK);

    when(languageClientFactory.createClient(url)).thenReturn(languageClient);
    when(languageClient.validate(request)).thenReturn(httpResponse);

    assertEquals(response, languageService.validateSnippet(snippet, languageVersionOk));
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
}
