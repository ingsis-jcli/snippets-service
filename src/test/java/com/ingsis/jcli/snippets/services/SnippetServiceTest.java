package com.ingsis.jcli.snippets.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.ingsis.jcli.snippets.common.exceptions.InvalidSnippetException;
import com.ingsis.jcli.snippets.common.language.LanguageError;
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

  private static final String languageOk = "printscript";
  private static final String versionOk = "1.1";
  private static final LanguageVersion languageVersionOk = new LanguageVersion(languageOk, versionOk);
  private final static String languageUrl = "http://printscript:8080/";
  
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
  void createSnippetOk() {
    String name = "name";
    String content = "content";
    Long userId = 123L;

    Snippet input = new Snippet(name, languageUrl, userId, languageVersionOk);
    Snippet expected = new Snippet(name, languageUrl, userId, languageVersionOk);
    expected.setId(1L);

    SnippetDto snippetDto = new SnippetDto(name, content, userId, languageOk, versionOk);

    when(snippetRepository.save(input)).thenReturn(expected);
    when(blobStorageService.uploadSnippet(content)).thenReturn(languageUrl);
    when(languageService.getLanguageVersion(languageOk, versionOk)).thenReturn(languageVersionOk);
    when(languageService.validateSnippet(snippetDto.getContent(), languageVersionOk))
        .thenReturn(new LanguageSuccess());

    assertEquals(expected, snippetService.createSnippet(snippetDto));
  }
  
  @Test
  void createSnippetException() {
    String name = "name";
    String content = "content";
    Long userId = 123L;
    String errorMessage = "error";
    
    Snippet expected = new Snippet(name, languageUrl, userId, languageVersionOk);
    expected.setId(1L);
    
    SnippetDto snippetDto = new SnippetDto(name, content, userId, languageOk, versionOk);
    
    when(languageService.getLanguageVersion(languageOk, versionOk)).thenReturn(languageVersionOk);
    when(languageService.validateSnippet(snippetDto.getContent(), languageVersionOk))
        .thenReturn(new LanguageError(errorMessage));
    
    InvalidSnippetException exception = assertThrows(InvalidSnippetException.class, () ->
        snippetService.createSnippet(snippetDto));
    
    assertEquals(errorMessage, exception.getError());
    assertEquals(languageVersionOk, exception.getLanguageVersion());
  }
  
  @Test
  void editSnippetOk() {
    String name = "name";
    String newContent = "new content";
    String oldUrl = "urlurlurl";
    String newUrl = "newnewnew";
    Long userId = 123L;
    Long snippetId = 1L;
    Snippet oldSnippet = new Snippet(name, oldUrl, userId, languageVersionOk);
    Snippet newSnippet = new Snippet(name, newUrl, userId, languageVersionOk);
    
    SnippetDto snippetDto = new SnippetDto(name, newContent, userId, languageOk, versionOk);
    
    when(snippetService.getSnippet(snippetId)).thenReturn(Optional.of(oldSnippet));
    when(blobStorageService.updateSnippet(oldUrl, newContent)).thenReturn(newUrl);
    when(languageService.getLanguageVersion(languageOk, versionOk)).thenReturn(languageVersionOk);
    //when(languageService.validateSnippet(snippetDto.getContent(), languageVersionOk)).thenReturn(new LanguageSuccess());
    when(snippetRepository.save(newSnippet)).thenReturn(newSnippet);
    
    assertEquals(newSnippet, snippetService.editSnippet(snippetId, snippetDto));
  }
}
