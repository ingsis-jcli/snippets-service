package com.ingsis.jcli.snippets.clients;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BucketRestTemplateFactory {
  private final RestTemplate restTemplate;
  private final String baseUrl = "http://asset_service:8080/v1";

  public BucketRestTemplateFactory(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public BucketRestClient createClient() {
    return new BucketRestClient(restTemplate, baseUrl);
  }
}
