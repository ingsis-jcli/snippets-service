package com.ingsis.jcli.snippets.clients.factory;

import com.ingsis.jcli.snippets.clients.LanguageRestClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LanguageRestTemplateFactory {

  private final RestTemplate restTemplate;

  public LanguageRestTemplateFactory(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public LanguageRestClient createClient(String baseUrl) {
    return new LanguageRestClient(restTemplate, baseUrl);
  }
}
