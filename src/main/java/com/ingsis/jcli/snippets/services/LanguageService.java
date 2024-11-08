package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.clients.LanguageRestClient;
import com.ingsis.jcli.snippets.clients.factory.LanguageRestTemplateFactory;
import com.ingsis.jcli.snippets.common.exceptions.NoSuchLanguageException;
import com.ingsis.jcli.snippets.common.language.LanguageError;
import com.ingsis.jcli.snippets.common.language.LanguageResponse;
import com.ingsis.jcli.snippets.common.language.LanguageSuccess;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.common.requests.FormatRequest;
import com.ingsis.jcli.snippets.common.requests.RuleDto;
import com.ingsis.jcli.snippets.common.requests.TestCaseRequest;
import com.ingsis.jcli.snippets.common.requests.TestState;
import com.ingsis.jcli.snippets.common.requests.TestType;
import com.ingsis.jcli.snippets.common.requests.ValidateRequest;
import com.ingsis.jcli.snippets.common.responses.ErrorResponse;
import com.ingsis.jcli.snippets.common.responses.FormatResponse;
import com.ingsis.jcli.snippets.config.LanguageProperties;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.models.TestCase;
import com.ingsis.jcli.snippets.repositories.SnippetRepository;
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
  private final Map<LanguageVersion, String> extensions;
  private final LanguageRestTemplateFactory languageRestTemplateFactory;
  private final SnippetRepository snippetRepository;

  @Autowired
  public LanguageService(
      LanguageProperties languageProperties,
      LanguageRestTemplateFactory languageRestTemplateFactory,
      SnippetRepository snippetRepository) {
    this.languageRestTemplateFactory = languageRestTemplateFactory;
    this.urls = languageProperties.getUrls();
    this.extensions =
        Map.of(
            new LanguageVersion("printscript", "1.0"), "ps",
            new LanguageVersion("printscript", "1.1"), "ps");
    this.snippetRepository = snippetRepository;
  }

  public LanguageVersion getLanguageVersion(String languageName, String versionName) {
    if (!urls.containsKey(languageName.toLowerCase())) {
      throw new NoSuchLanguageException(languageName);
    }

    return new LanguageVersion(languageName, versionName);
  }

  public void validateLanguage(String languageName) {
    if (!urls.containsKey(languageName.toLowerCase())) {
      throw new NoSuchLanguageException(languageName);
    }
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

  public FormatResponse formatSnippet(
      List<RuleDto> rules, Snippet snippet, LanguageVersion languageVersion) {
    String language = languageVersion.getLanguage();
    String version = languageVersion.getVersion();

    if (!urls.containsKey(language)) {
      throw new NoSuchLanguageException(language);
    }
    String baseUrl = urls.get(language);
    LanguageRestClient client = languageRestTemplateFactory.createClient(baseUrl);
    FormatRequest request = new FormatRequest(snippet.getName(), snippet.getUrl(), rules, version);
    FormatResponse response = client.format(request);
    snippet.getStatus().setFormatting(response.status());
    snippetRepository.save(snippet);
    return response;
  }

  public Map<LanguageVersion, String> getAllExtensions() {
    return extensions;
  }

  public String getExtension(LanguageVersion language) {
    if (!extensions.containsKey(language)) {
      throw new NoSuchLanguageException(language.getLanguage());
    }
    return extensions.get(language);
  }
}
