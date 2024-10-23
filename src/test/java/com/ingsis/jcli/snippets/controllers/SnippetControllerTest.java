package com.ingsis.jcli.snippets.controllers;

import static com.ingsis.jcli.snippets.services.BlobStorageService.getBaseUrl;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingsis.jcli.snippets.common.language.LanguageSuccess;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import com.ingsis.jcli.snippets.models.Snippet;
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

  @Autowired private ObjectMapper objectMapper;

  private static final String path = "/snippet";
  private static final LanguageVersion languageVersion = new LanguageVersion("printscript", "1.1");
  private static final String language = "printscript";
  private static final String version = "1.1";

  @Test
  void getSnippetOk() throws Exception {
    Long id = 1L;
    String userId = "123";
    String expectedSnippetContent = "This is the content of the snippet.";

    when(permissionService.hasPermissionOnSnippet(any(), anyLong(), anyString())).thenReturn(true);
    when(snippetService.getSnippet(id)).thenReturn(Optional.of(expectedSnippetContent));

    mockMvc
        .perform(
            get(path)
                .param("userId", userId)
                .param("snippetId", id.toString())
                .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().isOk())
        .andExpect(content().string(expectedSnippetContent));
  }

  @Test
  void getSnippetNotFound() throws Exception {
    Long id = 1L;

    when(permissionService.hasPermissionOnSnippet(any(), anyLong(), anyString())).thenReturn(true);
    when(snippetService.getSnippet(anyLong())).thenReturn(Optional.empty());

    mockMvc
        .perform(
            get(path)
                .param("userId", "123")
                .param("snippetId", id.toString())
                .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().isNotFound());
  }

  @Test
  void getSnippetForbidden() throws Exception {
    Long id = 1L;

    when(permissionService.hasPermissionOnSnippet(any(), anyLong(), anyString())).thenReturn(false);
    when(snippetService.getSnippet(anyLong())).thenReturn(Optional.of(""));

    mockMvc
        .perform(
            get(path)
                .param("userId", "123")
                .param("snippetId", id.toString())
                .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().isForbidden());
  }

  @Test
  void createSnippetSuccess() throws Exception {
    SnippetDto snippetDto = new SnippetDto("name", "content", "123", language, version);
    Snippet snippet = new Snippet("name", getBaseUrl(snippetDto), "123", languageVersion);

    Long id = 1L;
    snippet.setId(id);

    when(snippetService.createSnippet(snippetDto)).thenReturn(snippet);
    when(languageService.validateSnippet(snippetDto.getContent(), languageVersion))
        .thenReturn(new LanguageSuccess());

    mockMvc
        .perform(
            post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(snippetDto))
                .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$").value(id));
  }

  @Test
  void createSnippetFailBlankDto() throws Exception {
    SnippetDto snippetDto = new SnippetDto("", "", "123", "", "");

    mockMvc
        .perform(
            post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(snippetDto))
                .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void editSnippetSuccess() throws Exception {
    String userId = "123";
    Snippet snippet = new Snippet("name", "url", userId, languageVersion);
    SnippetDto snippetDto = new SnippetDto("name", "content", userId, language, version);

    Long id = 1L;
    snippet.setId(id);

    when(permissionService.hasPermissionOnSnippet(any(), anyLong(), anyString())).thenReturn(true);
    when(snippetService.editSnippet(id, snippetDto)).thenReturn(snippet);

    mockMvc
        .perform(
            put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(snippetDto))
                .param("userId", userId)
                .param("snippetId", id.toString())
                .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(id));
  }

  @Test
  void editSnippetFailForbidden() throws Exception {
    String userId = "123";
    Snippet snippet = new Snippet("name", "url", userId, languageVersion);
    SnippetDto snippetDto = new SnippetDto("name", "content", userId, language, version);

    Long id = 1L;
    snippet.setId(id);

    when(permissionService.hasPermissionOnSnippet(any(), anyLong(), anyString())).thenReturn(false);

    mockMvc
        .perform(
            put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(snippetDto))
                .param("userId", userId.toString())
                .param("snippetId", id.toString())
                .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().isForbidden());
  }
}
