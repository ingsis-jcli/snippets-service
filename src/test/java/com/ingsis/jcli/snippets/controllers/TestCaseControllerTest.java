package com.ingsis.jcli.snippets.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingsis.jcli.snippets.common.requests.TestType;
import com.ingsis.jcli.snippets.dto.TestCaseDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.services.JwtService;
import com.ingsis.jcli.snippets.services.PermissionService;
import com.ingsis.jcli.snippets.services.SnippetService;
import com.ingsis.jcli.snippets.services.TestCaseService;
import java.util.Arrays;
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

    Jwt mockJwt = createMockJwt("userId");

    when(jwtService.extractUserId(token)).thenReturn("userId");
    when(permissionService.hasPermissionOnSnippet(any(), any(), any())).thenReturn(true);
    when(snippetService.getSnippet(testCaseDto.snippetId())).thenReturn(Optional.of(snippet));
    when(testCaseService.createTestCase(testCaseDto, snippet)).thenReturn(1L);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            post("/test-case")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCaseDto))
                .header("Authorization", token))
        .andExpect(status().isCreated())
        .andExpect(content().string("1"));
  }

  @Test
  void testCreateTestCase_NoPermission() throws Exception {
    String token = "Bearer mock-token";
    TestCaseDto testCaseDto =
        new TestCaseDto(
            "Test Case", 1L, Arrays.asList("input1"), Arrays.asList("output1"), TestType.VALID);

    Jwt mockJwt = createMockJwt("userId");
    when(jwtService.extractUserId(token)).thenReturn("userId");
    when(permissionService.hasPermissionOnSnippet(any(), any(), any())).thenReturn(false);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            post("/test-case")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCaseDto))
                .header("Authorization", token))
        .andExpect(status().isForbidden());
  }

  @Test
  void testCreateTestCase_SnippetNotFound() throws Exception {
    String token = "Bearer mock-token";
    TestCaseDto testCaseDto =
        new TestCaseDto(
            "Test Case", 1L, Arrays.asList("input1"), Arrays.asList("output1"), TestType.VALID);

    Jwt mockJwt = createMockJwt("userId");
    when(jwtService.extractUserId(token)).thenReturn("userId");
    when(permissionService.hasPermissionOnSnippet(any(), any(), any())).thenReturn(true);
    when(snippetService.getSnippet(testCaseDto.snippetId())).thenReturn(Optional.empty());
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            post("/test-case")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCaseDto))
                .header("Authorization", token))
        .andExpect(status().isNotFound());
  }
}
