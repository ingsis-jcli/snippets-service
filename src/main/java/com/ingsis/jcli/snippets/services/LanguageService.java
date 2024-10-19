package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.clients.LanguageClient;
import com.ingsis.jcli.snippets.clients.factory.LanguageClientFactory;
import com.ingsis.jcli.snippets.common.exceptions.NoSuchLanguageException;
import com.ingsis.jcli.snippets.common.language.LanguageError;
import com.ingsis.jcli.snippets.common.language.LanguageResponse;
import com.ingsis.jcli.snippets.common.language.LanguageSuccess;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.common.requests.ValidateRequest;
import com.ingsis.jcli.snippets.config.LanguageUrlProperties;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LanguageService {
  private final LanguageClientFactory languageClientFactory;
  private final Map<String, String> urls;

  @Autowired
  public LanguageService(
      LanguageClientFactory languageClientFactory, LanguageUrlProperties languageUrlProperties) {
    this.languageClientFactory = languageClientFactory;

    this.urls = languageUrlProperties.getUrls();
  }

  public LanguageVersion getLanguageVersion(String languageName, String versionName) {
    if (!urls.containsKey(languageName)) {
      throw new NoSuchLanguageException(languageName);
    }

    return new LanguageVersion(languageName, versionName);
  }

  public LanguageResponse validateSnippet(String snippet, LanguageVersion languageVersion) {
    Marker marker = MarkerFactory.getMarker("Validate");
    log.info(marker, "Validating snippet: " + snippet);

    String language = languageVersion.getLanguage();
    String version = languageVersion.getVersion();
    log.info(marker, "Language: " + language + " Version: " + version);

    if (!urls.containsKey(language)) {
      log.error(marker, "NoSuchLanguageException: " + language + " - " + version);
      throw new NoSuchLanguageException(language);
    }

    String baseUrl = urls.get(language);
    LanguageClient client = languageClientFactory.createClient(baseUrl);
    log.info(marker, "Client base url: " + baseUrl);

    ValidateRequest validateRequest = new ValidateRequest(snippet, version);
    log.info(marker, "Validate request: " + validateRequest);

    ResponseEntity<String> response = client.validate(validateRequest);
    log.info(marker, "Response code: " + response.getStatusCode());
    
    return getResponse(response);
  }
  
  public LanguageResponse getResponse(ResponseEntity<String> response) {
    if (response.getStatusCode().is2xxSuccessful()) {
      return new LanguageSuccess();
    }
    return new LanguageError(response.getBody());
  }
}
