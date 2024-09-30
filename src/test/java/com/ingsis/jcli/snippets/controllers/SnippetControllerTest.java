package com.ingsis.jcli.snippets.controllers;

import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.services.PermissionService;
import com.ingsis.jcli.snippets.services.SnippetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SnippetControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SnippetService snippetService;

  @MockBean
  private PermissionService permissionService;

  private final static String path = "/snippet";

  @Test
  void getSnippetOk() throws Exception {
    Snippet snippet = new Snippet();
    Long id = 1L;
    snippet.setId(id);

    when(permissionService.canReadSnippet(anyLong(), anyLong())).thenReturn(true);
    when(snippetService.getSnippet(id)).thenReturn(Optional.of(snippet));
    mockMvc.perform(get(path)
            .param("userId", "123")
            .param("snippetId", id.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L));
  }

  @Test
  void getSnippetNotFound() throws Exception {
    Snippet snippet = new Snippet();
    Long id = 1L;
    snippet.setId(id);

    when(permissionService.canReadSnippet(anyLong(), anyLong())).thenReturn(true);
    when(snippetService.getSnippet(anyLong())).thenReturn(Optional.empty());
    mockMvc.perform(get(path)
            .param("userId", "123")
            .param("snippetId", id.toString()))
        .andExpect(status().isNotFound());
  }

  @Test
  void getSnippetForbidden() throws Exception {
    Snippet snippet = new Snippet();
    Long id = 1L;
    snippet.setId(id);

    when(permissionService.canReadSnippet(anyLong(), anyLong())).thenReturn(false);
    when(snippetService.getSnippet(anyLong())).thenReturn(Optional.of(snippet));
    mockMvc.perform(get(path)
            .param("userId", "123")
            .param("snippetId", id.toString()))
        .andExpect(status().isForbidden());
  }
}