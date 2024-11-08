package com.ingsis.jcli.snippets.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingsis.jcli.snippets.common.requests.TestState;
import com.ingsis.jcli.snippets.common.requests.TestType;
import com.ingsis.jcli.snippets.dto.TestCaseDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.models.TestCase;
import com.ingsis.jcli.snippets.services.JwtService;
import com.ingsis.jcli.snippets.services.LanguageService;
import com.ingsis.jcli.snippets.services.PermissionService;
import com.ingsis.jcli.snippets.services.SnippetService;
import com.ingsis.jcli.snippets.services.TestCaseService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TestCaseControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private SnippetService snippetService;
  @MockBean private PermissionService permissionService;
  @MockBean private JwtService jwtService;
  @MockBean private TestCaseService testCaseService;
  @MockBean private LanguageService languageService;
  @MockBean private JwtDecoder jwtDecoder;

  @Autowired private ObjectMapper objectMapper;

  private Jwt createMockJwt(String userId) {
    return Jwt.withTokenValue("mock-token")
        .header("alg", "none")
        .claim("user_id", userId)
        .claim("scope", "write")
        .build();
  }

  public TestCaseControllerTest() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateTestCase_Success() throws Exception {
    String token = "Bearer mock-token";
    TestCaseDto testCaseDto =
        new TestCaseDto(
            "Test Case", 1L, Arrays.asList("input1"), Arrays.asList("output1"), TestType.VALID);
    Snippet snippet = new Snippet();
    snippet.setId(1L);
    snippet.setOwner("userId");

    Jwt mockJwt = createMockJwt("userId");

    TestCase testCase =
        new TestCase(
            snippet,
            testCaseDto.name(),
            testCaseDto.input(),
            testCaseDto.output(),
            testCaseDto.type(),
            TestState.PENDING);

    when(jwtService.extractUserId(token)).thenReturn("userId");
    when(snippetService.isOwner(snippet, "userId")).thenReturn(true);
    when(snippetService.getSnippet(testCaseDto.snippetId())).thenReturn(Optional.of(snippet));
    when(testCaseService.createTestCase(testCaseDto, snippet)).thenReturn(testCase);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            post("/testcase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCaseDto))
                .header("Authorization", token))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value(testCase.getName()))
        .andExpect(jsonPath("$.type").value(testCase.getType().toString()))
        .andExpect(jsonPath("$.state").value(testCase.getState().toString()));
  }

  @Test
  void testCreateTestCase_SnippetNotFound() throws Exception {
    String token = "Bearer mock-token";
    TestCaseDto testCaseDto =
        new TestCaseDto(
            "Test Case", 1L, Arrays.asList("input1"), Arrays.asList("output1"), TestType.VALID);

    Jwt mockJwt = createMockJwt("userId");
    when(jwtService.extractUserId(token)).thenReturn("userId");
    when(permissionService.hasPermissionOnSnippet(any(), any())).thenReturn(true);
    when(snippetService.getSnippet(testCaseDto.snippetId())).thenReturn(Optional.empty());
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            post("/testcase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCaseDto))
                .header("Authorization", token))
        .andExpect(status().isNotFound());
  }

  @Test
  void testRunTestCase_NotFound() throws Exception {
    String token = "Bearer mock-token";
    Long testCaseId = 1L;

    Jwt mockJwt = createMockJwt("userId");

    when(jwtService.extractUserId(token)).thenReturn("userId");
    when(testCaseService.getTestCase(testCaseId)).thenReturn(Optional.empty());
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(get("/testcase/" + testCaseId).header("Authorization", token))
        .andExpect(status().isNotFound());
  }

  @Test
  void testRunTestCase_Success() throws Exception {
    String token = "Bearer mock-token";
    Long testCaseId = 1L;
    TestCase testCase = new TestCase();

    Snippet snippet = new Snippet();
    snippet.setId(1L);
    testCase.setSnippet(snippet);

    Jwt mockJwt = createMockJwt("userId");

    when(jwtService.extractUserId(token)).thenReturn("userId");
    when(testCaseService.getTestCase(testCaseId)).thenReturn(Optional.of(testCase));
    when(permissionService.hasPermissionOnSnippet(any(), any())).thenReturn(true);
    when(languageService.runTestCase(testCase)).thenReturn(TestState.SUCCESS);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(get("/testcase/" + testCaseId).header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(content().string("\"" + TestState.SUCCESS.toString() + "\""));
  }

  @Test
  void testGetTestCaseByUser_Success() throws Exception {
    String token = "Bearer mock-token";
    String userId = "userId";

    Jwt mockJwt = createMockJwt(userId);
    List<TestCase> testCases = Arrays.asList(new TestCase(), new TestCase());

    when(jwtService.extractUserId(token)).thenReturn(userId);
    when(testCaseService.getTestCaseByUser(userId)).thenReturn(testCases);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(get("/testcase").header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(testCases)));
  }

  @Test
  void testDeleteTestCase_Success() throws Exception {
    Long testCaseId = 1L;
    String userId = "123";

    Jwt mockJwt = createMockJwt(userId);

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(testCaseService.getTestCase(testCaseId)).thenReturn(Optional.of(new TestCase()));
    when(snippetService.isOwner(any(), anyString())).thenReturn(true);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(delete("/testcase/" + testCaseId).header("Authorization", "Bearer mock-token"))
        .andExpect(status().isOk());
  }
}
