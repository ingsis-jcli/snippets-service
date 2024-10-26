package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.clients.LanguageRestClient;
import com.ingsis.jcli.snippets.clients.LanguageRestTemplateFactory;
import com.ingsis.jcli.snippets.common.exceptions.NoSuchLanguageException;
import com.ingsis.jcli.snippets.common.language.LanguageError;
import com.ingsis.jcli.snippets.common.language.LanguageResponse;
import com.ingsis.jcli.snippets.common.language.LanguageSuccess;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.common.requests.RuleDto;
import com.ingsis.jcli.snippets.common.requests.TestCaseRequest;
import com.ingsis.jcli.snippets.common.requests.TestState;
import com.ingsis.jcli.snippets.common.requests.TestType;
import com.ingsis.jcli.snippets.common.requests.ValidateRequest;
import com.ingsis.jcli.snippets.common.responses.ErrorResponse;
import com.ingsis.jcli.snippets.config.LanguageUrlProperties;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.models.TestCase;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LanguageService {
  private final Map<String, String> urls;
  private final LanguageRestTemplateFactory languageRestTemplateFactory;

  @Autowired
  public LanguageService(
      LanguageUrlProperties languageUrlProperties,
      LanguageRestTemplateFactory languageRestTemplateFactory) {
    this.languageRestTemplateFactory = languageRestTemplateFactory;
    this.urls = languageUrlProperties.getUrls();
  }

  public LanguageVersion getLanguageVersion(String languageName, String versionName) {
    if (!urls.containsKey(languageName)) {
      throw new NoSuchLanguageException(languageName);
    }

    return new LanguageVersion(languageName, versionName);
  }

  public LanguageResponse validateSnippet(Snippet snippet, LanguageVersion languageVersion) {
    Marker marker = MarkerFactory.getMarker("Validate");
    log.info(marker, "Validating snippet: " + snippet.getName());

    String language = languageVersion.getLanguage();
    String version = languageVersion.getVersion();
    log.info(marker, "Language: " + language + " Version: " + version);

    if (!urls.containsKey(language)) {
      log.error(marker, "NoSuchLanguageException: " + language + " - " + version);
      throw new NoSuchLanguageException(language);
    }

    String baseUrl = urls.get(language);
    LanguageRestClient client = languageRestTemplateFactory.createClient(baseUrl);
    log.info(marker, "Client base url: " + baseUrl);

    ValidateRequest validateRequest =
        new ValidateRequest(snippet.getName(), snippet.getUrl(), version);
    log.info(marker, "Validate request: " + validateRequest);

    ErrorResponse response = client.validate(validateRequest);

    log.info(marker, "Response: " + response);

    if (response.hasError()) {
      return new LanguageError(response.error());
    }
    return new LanguageSuccess();
  }

  public List<RuleDto> getFormattingRules(LanguageVersion languageVersion) {
    Marker marker = MarkerFactory.getMarker("Get Rules");
    log.info(marker, "Getting rules for language version: " + languageVersion);

    String language = languageVersion.getLanguage();
    String version = languageVersion.getVersion();

    if (!urls.containsKey(language)) {
      throw new NoSuchLanguageException(language);
    }
    String baseUrl = urls.get(language);
    log.info(marker, "Base url: " + baseUrl);

    LanguageRestClient client = languageRestTemplateFactory.createClient(baseUrl);
    List<RuleDto> response = client.getFormattingRules(version);
    log.info(marker, "Response from language: " + response);

    return response;
  }

  public List<RuleDto> getLintingRules(LanguageVersion languageVersion) {
    Marker marker = MarkerFactory.getMarker("Get Rules");
    log.info(marker, "Getting rules for language version: " + languageVersion);

    String language = languageVersion.getLanguage();
    String version = languageVersion.getVersion();

    if (!urls.containsKey(language)) {
      throw new NoSuchLanguageException(language);
    }
    String baseUrl = urls.get(language);
    log.info(marker, "Base url: " + baseUrl);

    LanguageRestClient client = languageRestTemplateFactory.createClient(baseUrl);
    List<RuleDto> response = client.getLintingRules(version);
    log.info(marker, "Response from language: " + response);

    return response;
  }

  public TestState runTestCase(TestCase testCase) {
    LanguageVersion languageVersion = testCase.getSnippet().getLanguageVersion();
    String language = languageVersion.getLanguage();
    String version = languageVersion.getVersion();

    if (!urls.containsKey(language)) {
      throw new NoSuchLanguageException(language);
    }

    String baseUrl = urls.get(language);

    LanguageRestClient client = languageRestTemplateFactory.createClient(baseUrl);

    String snippetName = testCase.getSnippet().getName();
    String url = testCase.getSnippet().getUrl();
    List<String> input = testCase.getInputs();
    List<String> output = testCase.getOutputs();

    TestType result =
        client.runTestCase(new TestCaseRequest(snippetName, url, version, input, output));

    if (result.equals(testCase.getType())) {
      return TestState.SUCCESS;
    }
    return TestState.FAILURE;
  }
}
