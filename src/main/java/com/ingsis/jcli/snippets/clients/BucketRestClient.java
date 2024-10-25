package com.ingsis.jcli.snippets.clients;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class BucketRestClient {

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public BucketRestClient(RestTemplate restTemplate, String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  public String getSnippet(String container, String key) {
    String url = String.format("%s/asset/%s-%s", baseUrl, container, key);

    ResponseEntity<String> response =
        restTemplate.exchange(url, HttpMethod.GET, null, String.class);

    return response.getBody();
  }

  public void saveSnippet(String container, String key, String content) {
    String url = String.format("%s/asset/%s-%s", baseUrl, container, key);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> requestEntity = new HttpEntity<>(content, headers);

    restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Void.class);
  }

  public void deleteSnippet(String container, String key) {
    String url = String.format("%s/asset/%s-%s", baseUrl, container, key);

    restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
  }
}
