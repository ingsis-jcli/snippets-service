package com.ingsis.jcli.snippets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.ingsis.jcli.snippets.common.language.LanguageSuccess;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.repositories.SnippetRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SnippetServiceTest {

  @Autowired private SnippetService snippetService;

  @MockBean private SnippetRepository snippetRepository;

  @MockBean private BlobStorageService blobStorageService;

  @MockBean private LanguageService languageService;

  private static final LanguageVersion languageVersion = new LanguageVersion("printscript", "1.1");

  private static final String language = "printscript";

  private static final String version = "1.1";

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

  @Test
  void createSnippet() {
    String name = "name";
    String url = "url";
    String content = "content";
    Long userId = 123L;

    Snippet input = new Snippet(name, url, userId, languageVersion);
    Snippet expected = new Snippet(name, url, userId, languageVersion);
    expected.setId(1L);

    SnippetDto snippetDto = new SnippetDto(name, content, userId, language, version);

    when(snippetRepository.save(input)).thenReturn(expected);
    when(blobStorageService.uploadSnippet(content)).thenReturn(url);
    when(languageService.getLanguageVersion(language, version)).thenReturn(languageVersion);
    when(languageService.validateSnippet(snippetDto.getContent(), languageVersion))
        .thenReturn(new LanguageSuccess());

    assertEquals(expected, snippetService.createSnippet(snippetDto));
  }
}
