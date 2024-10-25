package com.ingsis.jcli.snippets.clients;

import com.ingsis.jcli.snippets.common.requests.ValidateRequest;
import com.ingsis.jcli.snippets.common.responses.DefaultRule;
import com.ingsis.jcli.snippets.common.responses.ErrorResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
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

  public ErrorResponse validate(ValidateRequest validateRequest) {
    String url = String.format("%s/validate", baseUrl);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<ValidateRequest> requestEntity = new HttpEntity<>(validateRequest, headers);
    ResponseEntity<ErrorResponse> response =
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, ErrorResponse.class);
    return response.getBody();
  }
}
