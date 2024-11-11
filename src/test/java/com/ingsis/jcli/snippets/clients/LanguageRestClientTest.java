package com.ingsis.jcli.snippets.clients;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingsis.jcli.snippets.common.requests.AnalyzeRequest;
import com.ingsis.jcli.snippets.common.requests.FormatRequest;
import com.ingsis.jcli.snippets.common.requests.RuleDto;
import com.ingsis.jcli.snippets.common.requests.TestCaseRequest;
import com.ingsis.jcli.snippets.common.requests.TestType;
import com.ingsis.jcli.snippets.common.requests.ValidateRequest;
import com.ingsis.jcli.snippets.common.responses.ErrorResponse;
import com.ingsis.jcli.snippets.common.responses.FormatResponse;
import com.ingsis.jcli.snippets.common.status.ProcessStatus;
import com.ingsis.jcli.snippets.services.JwtService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class LanguageRestClientTest {

  @Mock private RestTemplate restTemplate;

  @InjectMocks private LanguageRestClient languageRestClient;

  @MockBean private JwtDecoder jwtDecoder;
  @MockBean private JwtService jwtService;

  @Autowired private ObjectMapper objectMapper;

  private final String baseUrl = "http://localhost:8080";

  @BeforeEach
  void setUp() {
    languageRestClient = new LanguageRestClient(restTemplate, baseUrl);
  }

  @Test
  void testGetLintingRules() {
    List<RuleDto> mockRules = Collections.singletonList(new RuleDto(true, "rule1", "value1"));
    ResponseEntity<List<RuleDto>> responseEntity = ResponseEntity.ok(mockRules);

    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
        .thenReturn(responseEntity);

    List<RuleDto> result = languageRestClient.getLintingRules("1.1");
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("rule1", result.get(0).name());
  }

  @Test
  void testGetFormattingRules() {
    List<RuleDto> mockRules = Collections.singletonList(new RuleDto(true, "formatRule", "value1"));
    ResponseEntity<List<RuleDto>> responseEntity = ResponseEntity.ok(mockRules);

    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
        .thenReturn(responseEntity);

    List<RuleDto> result = languageRestClient.getFormattingRules("1.1");
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("formatRule", result.get(0).name());
  }

  @Test
  void testValidate() {
    ValidateRequest validateRequest = new ValidateRequest("name", "url", "1.1");
    ResponseEntity<ErrorResponse> responseEntity = ResponseEntity.ok(new ErrorResponse());

    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ErrorResponse.class)))
        .thenReturn(responseEntity);

    ErrorResponse result = languageRestClient.validate(validateRequest);
    assertNotNull(result);
  }

  @Test
  void testRunTestCase() {
    TestCaseRequest testCaseRequest =
        new TestCaseRequest("name", "url", "1.1", Collections.emptyList(), Collections.emptyList());
    TestType expectedTestType = TestType.VALID;
    ResponseEntity<TestType> responseEntity = ResponseEntity.ok(expectedTestType);

    when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)))
        .thenReturn(responseEntity);

    TestType result = languageRestClient.runTestCase(testCaseRequest);
    assertNotNull(result);
    assertEquals(TestType.VALID, result);
  }

  @Test
  void testFormat() {
    FormatRequest formatRequest = new FormatRequest("input code", "language", List.of(), "version");
    FormatResponse mockFormatResponse =
        new FormatResponse("formatted code", ProcessStatus.COMPLIANT);
    ResponseEntity<FormatResponse> responseEntity = ResponseEntity.ok(mockFormatResponse);

    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(FormatResponse.class)))
        .thenReturn(responseEntity);

    FormatResponse result = languageRestClient.format(formatRequest);

    assertNotNull(result);
    assertEquals("formatted code", result.content());
  }

  @Test
  void testLint() {
    AnalyzeRequest analyzeRequest =
        new AnalyzeRequest("input code", "language", List.of(), "version");
    ErrorResponse errorResponse = new ErrorResponse();
    ResponseEntity<ErrorResponse> responseEntity = ResponseEntity.ok(errorResponse);

    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ErrorResponse.class)))
        .thenReturn(responseEntity);

    ErrorResponse result =
        languageRestClient.analyze(
            analyzeRequest.name(),
            analyzeRequest.url(),
            analyzeRequest.rules(),
            analyzeRequest.version());

    assertNotNull(result);
    assertFalse(result.hasError());
  }
}
