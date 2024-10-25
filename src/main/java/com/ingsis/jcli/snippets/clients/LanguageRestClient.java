package com.ingsis.jcli.snippets.clients;

import com.ingsis.jcli.snippets.common.requests.RuleDto;
import com.ingsis.jcli.snippets.common.requests.ValidateRequest;
import com.ingsis.jcli.snippets.common.responses.ErrorResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class LanguageRestClient {

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public LanguageRestClient(RestTemplate restTemplate, String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  public List<RuleDto> getLintingRules(String version) {
    String url = String.format("%s/linting_rules?version=%s", baseUrl, version);

    ResponseEntity<List<RuleDto>> response =
        restTemplate.exchange(
            url, HttpMethod.GET, null, new ParameterizedTypeReference<List<RuleDto>>() {});

    return response.getBody();
  }

  public List<RuleDto> getFormattingRules(String version) {
    String url = String.format("%s/formatting_rules?version=%s", baseUrl, version);
    ResponseEntity<List<RuleDto>> response =
        restTemplate.exchange(
            url, HttpMethod.GET, null, new ParameterizedTypeReference<List<RuleDto>>() {});

    return response.getBody();
  }

  public ErrorResponse validate(ValidateRequest validateRequest) {
    String url = String.format("%s/validate", baseUrl);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<ValidateRequest> requestEntity = new HttpEntity<>(validateRequest, headers);
    ResponseEntity<ErrorResponse> response =
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, ErrorResponse.class);
    if (response.getStatusCode() == HttpStatus.OK) {
      return new ErrorResponse("");
    }
    if (response.getBody() == null) {
      return new ErrorResponse("No response received");
    }
    return response.getBody();
  }
}
