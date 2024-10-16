package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.clients.LanguageClient;
import com.ingsis.jcli.snippets.clients.factory.LanguageClientFactory;
import com.ingsis.jcli.snippets.common.exceptions.NoSuchLanguageException;
import com.ingsis.jcli.snippets.common.language.LanguageResponse;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.common.requests.ValidateRequest;
import com.ingsis.jcli.snippets.config.LanguageUrlProperties;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    String language = languageVersion.getLanguage();
    String version = languageVersion.getVersion();

    if (!urls.containsKey(language)) {
      throw new NoSuchLanguageException(language);
    }

    String baseUrl = urls.get(language);
    LanguageClient client = languageClientFactory.createClient(baseUrl);

    ResponseEntity<LanguageResponse> response =
        client.validate(new ValidateRequest(snippet, version));

    return response.getBody();
  }
}
