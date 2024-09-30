package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.repositories.SnippetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class SnippetServiceTest {

  @Autowired
  private SnippetService snippetService;

  @MockBean
  private SnippetRepository snippetRepository;

  @Test
  void getSnippet() {
    Snippet snippet = new Snippet();
    Long id = 1L;
    snippet.setId(id);

    when(snippetRepository.findSnippetById(id)).thenReturn(Optional.of(snippet));
    assertTrue(snippetService.getSnippet(id).isPresent());
  }

  @Test
  void getSnippetNull() {
    Long id = 1L;

    when(snippetRepository.findSnippetById(id)).thenReturn(Optional.empty());
    assertTrue(snippetService.getSnippet(id).isEmpty());
  }
}