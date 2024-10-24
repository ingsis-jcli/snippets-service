package com.ingsis.jcli.snippets.clients;

import com.ingsis.jcli.snippets.common.responses.DefaultRules;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class LanguageRestClient {

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public LanguageRestClient(RestTemplate restTemplate, String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  public DefaultRules getLintingRules(String version) {
    String url = String.format("%s/linting_rules?version=%s", baseUrl, version);
    ResponseEntity<DefaultRules> response = restTemplate.getForEntity(url, DefaultRules.class);

    return response.getBody();
  }

  public DefaultRules getFormattingRules(String version) {
    String url = String.format("%s/formatting_rules?version=%s", baseUrl, version);
    ResponseEntity<DefaultRules> response = restTemplate.getForEntity(url, DefaultRules.class);

    return response.getBody();
  }
}
