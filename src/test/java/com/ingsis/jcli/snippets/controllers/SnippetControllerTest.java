package com.ingsis.jcli.snippets.controllers;

import static com.ingsis.jcli.snippets.services.BlobStorageService.getBaseUrl;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingsis.jcli.snippets.common.PermissionType;
import com.ingsis.jcli.snippets.common.language.LanguageSuccess;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.services.JwtService;
import com.ingsis.jcli.snippets.services.LanguageService;
import com.ingsis.jcli.snippets.services.PermissionService;
import com.ingsis.jcli.snippets.services.SnippetService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SnippetControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private SnippetService snippetService;
  @MockBean private PermissionService permissionService;
  @MockBean private LanguageService languageService;
  @MockBean private JwtDecoder jwtDecoder;
  @MockBean private JwtService jwtService;

  @Autowired private ObjectMapper objectMapper;

  private static final String path = "/snippet";
  private static final LanguageVersion languageVersion = new LanguageVersion("printscript", "1.1");

  private Jwt createMockJwt(String userId) {
    return Jwt.withTokenValue("mock-token")
        .header("alg", "none")
        .claim("user_id", userId)
        .claim("scope", "read")
        .build();
  }

  @Test
  void getSnippetOk() throws Exception {
    Long id = 1L;
    String userId = "123";
    String expectedSnippetContent = "This is the content of the snippet.";

    Jwt mockJwt = createMockJwt(userId);

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(permissionService.hasPermissionOnSnippet(any(), anyLong(), anyString())).thenReturn(true);
    when(snippetService.getSnippet(id)).thenReturn(Optional.of(expectedSnippetContent));
    when(jwtDecoder.decode(anyString()))
        .thenReturn(mockJwt); // Mock JwtDecoder to return the mockJwt

    mockMvc
        .perform(
            get(path)
                .param("snippetId", id.toString())
                .header("Authorization", "Bearer mock-token") // Set the Authorization header
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isOk())
        .andExpect(content().string(expectedSnippetContent));
  }

  @Test
  void getSnippetNotFound() throws Exception {
    Long id = 1L;
    String userId = "123";

    Jwt mockJwt = createMockJwt(userId);

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(permissionService.hasPermissionOnSnippet(PermissionType.READ, id, userId))
        .thenReturn(true);
    when(snippetService.getSnippet(id)).thenReturn(Optional.empty());
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            get(path)
                .param("snippetId", id.toString())
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isNotFound());
  }

  @Test
  void getSnippetForbidden() throws Exception {
    Long id = 1L;
    String userId = "123";

    Jwt mockJwt = createMockJwt(userId);

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(permissionService.hasPermissionOnSnippet(any(), anyLong(), anyString())).thenReturn(false);
    when(snippetService.getSnippet(id)).thenReturn(Optional.of(""));
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            get(path)
                .param("snippetId", id.toString())
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isForbidden());
  }

  @Test
  void createSnippetSuccess() throws Exception {
    String userId = "123";
    SnippetDto snippetDto = new SnippetDto("name", "content", userId, "printscript", "1.1");
    Snippet snippet = new Snippet("name", getBaseUrl(snippetDto, userId), userId, languageVersion);
    snippet.setId(1L);

    Jwt mockJwt = createMockJwt(userId);

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(snippetService.createSnippet(snippetDto, userId)).thenReturn(snippet);
    when(languageService.validateSnippet(snippet, languageVersion))
        .thenReturn(new LanguageSuccess());
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(snippetDto))
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$").value(1L));
  }

  @Test
  void createSnippetFailBlankDto() throws Exception {
    SnippetDto snippetDto = new SnippetDto("", "", "123", "", "");

    Jwt mockJwt = createMockJwt("123");
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(snippetDto))
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void editSnippetSuccess() throws Exception {
    Long id = 1L;
    String userId = "123";
    SnippetDto snippetDto = new SnippetDto("name", "content", userId, "printscript", "1.1");
    Snippet snippet = new Snippet("name", "url", userId, languageVersion);
    snippet.setId(id);

    Jwt mockJwt = createMockJwt(userId);

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(permissionService.hasPermissionOnSnippet(PermissionType.WRITE, id, userId))
        .thenReturn(true);
    when(snippetService.editSnippet(id, snippetDto, userId)).thenReturn(snippet);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(snippetDto))
                .param("snippetId", id.toString())
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(id));
  }

  @Test
  void editSnippetFailForbidden() throws Exception {
    Long id = 1L;
    String userId = "123";
    SnippetDto snippetDto = new SnippetDto("name", "content", userId, "printscript", "1.1");

    Jwt mockJwt = createMockJwt(userId);

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(permissionService.hasPermissionOnSnippet(PermissionType.WRITE, id, userId))
        .thenReturn(false);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(snippetDto))
                .param("snippetId", id.toString())
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isForbidden());
  }
}
