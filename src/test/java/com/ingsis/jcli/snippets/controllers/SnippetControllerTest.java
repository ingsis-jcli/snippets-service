package com.ingsis.jcli.snippets.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

  @Autowired private ObjectMapper objectMapper;

  private static final String path = "/snippet";

  private static final LanguageVersion languageVersion = new LanguageVersion("printscript", "1.1");

  private static final String language = "printscript";

  private static final String version = "1.1";

  @Test
  void getSnippetOk() throws Exception {
    Snippet snippet = new Snippet();
    Long id = 1L;
    snippet.setId(id);

    when(permissionService.hasPermissionOnSnippet(any(), anyLong(), anyLong())).thenReturn(true);
    when(snippetService.getSnippet(id)).thenReturn(Optional.of(snippet));
    mockMvc
        .perform(get(path).param("userId", "123").param("snippetId", id.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L));
  }

  @Test
  void getSnippetNotFound() throws Exception {
    Snippet snippet = new Snippet();
    Long id = 1L;
    snippet.setId(id);

    when(permissionService.hasPermissionOnSnippet(any(), anyLong(), anyLong())).thenReturn(true);
    when(snippetService.getSnippet(anyLong())).thenReturn(Optional.empty());
    mockMvc
        .perform(get(path).param("userId", "123").param("snippetId", id.toString()))
        .andExpect(status().isNotFound());
  }

  @Test
  void getSnippetForbidden() throws Exception {
    Snippet snippet = new Snippet();
    Long id = 1L;
    snippet.setId(id);

    when(permissionService.hasPermissionOnSnippet(any(), anyLong(), anyLong())).thenReturn(false);
    when(snippetService.getSnippet(anyLong())).thenReturn(Optional.of(snippet));
    mockMvc
        .perform(get(path).param("userId", "123").param("snippetId", id.toString()))
        .andExpect(status().isForbidden());
  }

  @Test
  void createSnippetSuccess() throws Exception {
    Snippet snippet = new Snippet("name", "url", 123L, languageVersion);
    SnippetDto snippetDto = new SnippetDto("name", "content", 123L, language, version);

    Long id = 1L;
    snippet.setId(id);

    when(snippetService.createSnippet(snippetDto)).thenReturn(snippet);
    when(languageService.validateSnippet(snippetDto.getContent(), languageVersion))
        .thenReturn(new LanguageSuccess());

    mockMvc
        .perform(
            post(path + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(snippetDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$").value(id));
  }

  @Test
  void createSnippetFailBlankDto() throws Exception {
    SnippetDto snippetDto = new SnippetDto("", "", 123L, "", "");

    mockMvc
        .perform(
            post(path + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(snippetDto)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void createSnippetFailEmptyDto() throws Exception {
    SnippetDto snippetDto = new SnippetDto();

    mockMvc
        .perform(
            post(path + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(snippetDto)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void createSnippetFailContent() throws Exception {
    Snippet snippet = new Snippet("name", "url", 123L, languageVersion);
    SnippetDto snippetDto = new SnippetDto("name", "content", 123L, language, version);

    Long id = 1L;
    snippet.setId(id);

    when(snippetService.createSnippet(snippetDto)).thenReturn(snippet);

    mockMvc.perform(post(path + "/create")).andExpect(status().is4xxClientError());
  }
  
  @Test
  void editSnippetSuccess() throws Exception {
    Long userId = 123L;
    Snippet snippet = new Snippet("name", "url", userId, languageVersion);
    SnippetDto snippetDto = new SnippetDto("name", "content", userId, language, version);
    
    Long id = 1L;
    snippet.setId(id);
    
    when(permissionService.hasPermissionOnSnippet(any(), anyLong(), anyLong())).thenReturn(true);
    when(snippetService.editSnippet(id, snippetDto)).thenReturn(snippet);
    
    mockMvc
        .perform(
            put(path + "/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(snippetDto))
                .param("userId", userId.toString())
                .param("snippetId", id.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(id));
  }
  
  @Test
  void editSnippetFail() throws Exception {
    Long userId = 123L;
    Snippet snippet = new Snippet("name", "url", userId, languageVersion);
    SnippetDto snippetDto = new SnippetDto("name", "content", userId, language, version);
    
    Long id = 1L;
    snippet.setId(id);
    
    when(permissionService.hasPermissionOnSnippet(any(), anyLong(), anyLong())).thenReturn(true);
    when(snippetService.editSnippet(id, snippetDto)).thenThrow(new RuntimeException());
    
    mockMvc
        .perform(
            put(path + "/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(snippetDto))
                .param("userId", userId.toString())
                .param("snippetId", id.toString()))
        .andExpect(status().isInternalServerError());
  }
  
  @Test
  void editSnippetFailForbidden() throws Exception {
    Long userId = 123L;
    Snippet snippet = new Snippet("name", "url", userId, languageVersion);
    SnippetDto snippetDto = new SnippetDto("name", "content", userId, language, version);
    
    Long id = 1L;
    snippet.setId(id);
    
    when(permissionService.hasPermissionOnSnippet(any(), anyLong(), anyLong())).thenReturn(false);
    
    mockMvc
        .perform(
            put(path + "/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(snippetDto))
                .param("userId", userId.toString())
                .param("snippetId", id.toString()))
        .andExpect(status().isForbidden());
  }
}
