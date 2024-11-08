package com.ingsis.jcli.snippets.clients;

import com.ingsis.jcli.snippets.common.requests.AnalyzeRequest;
import com.ingsis.jcli.snippets.common.requests.FormatRequest;
import com.ingsis.jcli.snippets.common.requests.RuleDto;
import com.ingsis.jcli.snippets.common.requests.TestCaseRequest;
import com.ingsis.jcli.snippets.common.requests.TestType;
import com.ingsis.jcli.snippets.common.requests.ValidateRequest;
import com.ingsis.jcli.snippets.common.responses.ErrorResponse;
import com.ingsis.jcli.snippets.common.responses.FormatResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
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

    try {
      ResponseEntity<ErrorResponse> response =
          restTemplate.exchange(url, HttpMethod.POST, requestEntity, ErrorResponse.class);
      if (response.getStatusCode() == HttpStatus.OK) {
        return new ErrorResponse();
      }
      if (response.getBody() == null) {
        return new ErrorResponse("No response received");
      }
      System.out.println("Received: " + response.getBody());
      return response.getBody();
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
        System.out.println("Bad request during validation: " + e.getResponseBodyAsString());
        return new ErrorResponse("Bad request: " + e.getResponseBodyAsString());
      } else {
        return new ErrorResponse("Client error: " + e.getResponseBodyAsString());
      }
    } catch (Exception e) {
      return new ErrorResponse("Unexpected error occurred: " + e.getMessage());
    }
  }

  public FormatResponse format(FormatRequest formatRequest) {
    String url = String.format("%s/format", baseUrl);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<FormatRequest> requestEntity = new HttpEntity<>(formatRequest, headers);
    ResponseEntity<FormatResponse> response =
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, FormatResponse.class);
    return response.getBody();
  }

  public TestType runTestCase(TestCaseRequest testCaseRequest) {
    String url = String.format("%s/test", baseUrl);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<TestCaseRequest> requestEntity = new HttpEntity<>(testCaseRequest, headers);

    ResponseEntity<TestType> response =
        restTemplate.exchange(
            url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<TestType>() {});

    return response.getBody();
  }

  public ErrorResponse analyze(String name, String url, List<RuleDto> rules, String version) {
    String endpointUrl = String.format("%s/analyze", baseUrl);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    AnalyzeRequest analyzeRequest = new AnalyzeRequest(name, url, rules, version);
    HttpEntity<AnalyzeRequest> requestEntity = new HttpEntity<>(analyzeRequest, headers);

    try {
      ResponseEntity<ErrorResponse> response =
          restTemplate.exchange(endpointUrl, HttpMethod.POST, requestEntity, ErrorResponse.class);

      if (response.getStatusCode() == HttpStatus.OK) {
        return response.getBody() != null
            ? response.getBody()
            : new ErrorResponse();
      } else {
        return new ErrorResponse("Unexpected response status: " + response.getStatusCode());
      }
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
        return new ErrorResponse("Bad request: " + e.getResponseBodyAsString());
      } else {
        return new ErrorResponse("Client error: " + e.getResponseBodyAsString());
      }
    } catch (Exception e) {
      return new ErrorResponse("Unexpected error occurred: " + e.getMessage());
    }
  }
}
