package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.clients.LanguageClient;
import com.ingsis.jcli.snippets.common.exceptions.NoSuchLanguageException;
import com.ingsis.jcli.snippets.common.language.LanguageResponse;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.config.LanguageUrlProperties;
import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LanguageService {
  private final LanguageClient languageClient;
  private final Map<String, String> urls;

  @Autowired
  public LanguageService(
      LanguageClient languageClient,
      LanguageUrlProperties languageUrlProperties) {
    this.languageClient = languageClient;

    this.urls = Map.of(
        "printscript", languageUrlProperties.getPrintscript()
    );
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

    URI baseUrl;
    try {
      String url = urls.get(language);
      baseUrl = URI.create(url);
    } catch (NoSuchElementException e) {
      throw new NoSuchLanguageException(language);
    }

    return languageClient.validate(baseUrl, snippet, version);
  }
}
