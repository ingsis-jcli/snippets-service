package com.ingsis.jcli.snippets.clients;

import com.ingsis.jcli.snippets.common.responses.DefaultRule;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class LanguageRestClient {

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public LanguageRestClient(RestTemplate restTemplate, String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  public List<DefaultRule> getLintingRules(String version) {
    String url = String.format("%s/linting_rules?version=%s", baseUrl, version);

    ResponseEntity<List<DefaultRule>> response =
        restTemplate.exchange(
            url, HttpMethod.GET, null, new ParameterizedTypeReference<List<DefaultRule>>() {});

    return response.getBody();
  }

  public List<DefaultRule> getFormattingRules(String version) {
    String url = String.format("%s/formatting_rules?version=%s", baseUrl, version);
    ResponseEntity<List<DefaultRule>> response =
        restTemplate.exchange(
            url, HttpMethod.GET, null, new ParameterizedTypeReference<List<DefaultRule>>() {});

    return response.getBody();
  }
}
