package com.ingsis.jcli.snippets.services;

import static com.ingsis.jcli.snippets.services.BlobStorageService.getBaseUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ingsis.jcli.snippets.common.exceptions.InvalidSnippetException;
import com.ingsis.jcli.snippets.common.language.LanguageError;
import com.ingsis.jcli.snippets.common.language.LanguageSuccess;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.repositories.SnippetRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SnippetServiceTest {

  @Autowired private SnippetService snippetService;

  @MockBean private SnippetRepository snippetRepository;

  @MockBean private BlobStorageService blobStorageService;

  @MockBean private LanguageService languageService;

  @MockBean private JwtDecoder jwtDecoder;

  private static final String languageOk = "printscript";
  private static final String versionOk = "1.1";
  private static final LanguageVersion languageVersionOk =
      new LanguageVersion(languageOk, versionOk);
  private static final String userId = "123";

  @Test
  void getSnippet() {
    Long id = 1L;
    Snippet snippet = new Snippet();
    snippet.setId(id);
    snippet.setName("Test Snippet");
    snippet.setUrl("http://example.com");

    when(snippetRepository.findSnippetById(id)).thenReturn(Optional.of(snippet));
    when(blobStorageService.getSnippet(snippet.getUrl(), snippet.getName()))
        .thenReturn(Optional.of("Snippet content"));

    Optional<String> result = snippetService.getSnippet(id);
    assertTrue(result.isPresent());
    assertEquals("Snippet content", result.get());
  }

  @Test
  void getSnippetNull() {
    Long id = 1L;

    when(snippetRepository.findSnippetById(id)).thenReturn(Optional.empty());
    Optional<String> result = snippetService.getSnippet(id);
    assertTrue(result.isEmpty());
  }

  @Test
  void createSnippetOk() {
    Long snippetId = 1L;
    String name = "name";
    String content = "content";
    String userId = "123";

    SnippetDto snippetDto = new SnippetDto(name, content, userId, languageOk, versionOk);
    Snippet expected = new Snippet(name, getBaseUrl(snippetDto), userId, languageVersionOk);
    expected.setId(1L);

    when(snippetRepository.save(any(Snippet.class)))
        .thenAnswer(
            invocation -> {
              Snippet savedSnippet = invocation.getArgument(0);
              savedSnippet.setId(snippetId);
              return savedSnippet;
            });
    when(languageService.getLanguageVersion(languageOk, versionOk)).thenReturn(languageVersionOk);
    when(languageService.validateSnippet(any(Snippet.class), any(LanguageVersion.class)))
        .thenReturn(new LanguageSuccess());

    Snippet actualSnippet = snippetService.createSnippet(snippetDto);
    assertEquals(expected, actualSnippet);
    verify(blobStorageService).uploadSnippet(getBaseUrl(snippetDto), name, content);
  }

  @Test
  void createSnippetException() {
    String name = "name";
    String content = "content";
    String errorMessage = "Invalid snippet error";

    SnippetDto snippetDto = new SnippetDto(name, content, userId, languageOk, versionOk);
    Snippet snippet = new Snippet(name, "url", userId, languageVersionOk);

    when(languageService.getLanguageVersion(languageOk, versionOk)).thenReturn(languageVersionOk);

    when(languageService.validateSnippet(any(Snippet.class), any(LanguageVersion.class)))
        .thenReturn(new LanguageError(errorMessage));

    InvalidSnippetException exception =
        assertThrows(InvalidSnippetException.class, () -> snippetService.createSnippet(snippetDto));

    assertEquals(errorMessage, exception.getError());
    assertEquals(languageVersionOk, exception.getLanguageVersion());
  }

  @Test
  void editSnippetOk() {
    Long snippetId = 1L;
    String initialName = "name";
    String initialUrl = "snippets/printscript-1.1-123";
    String userId = "123";

    Snippet initialSnippet = new Snippet(initialName, initialUrl, userId, languageVersionOk);
    initialSnippet.setId(snippetId);

    SnippetDto snippetDto2 = new SnippetDto("name2", "content2", "1234", languageOk, versionOk);
    String newUrl = getBaseUrl(snippetDto2);

    Snippet finalSnippet = new Snippet("name2", newUrl, "1234", languageVersionOk);
    finalSnippet.setId(snippetId);

    when(snippetRepository.findSnippetById(snippetId)).thenReturn(Optional.of(initialSnippet));
    when(languageService.getLanguageVersion(languageOk, versionOk)).thenReturn(languageVersionOk);
    when(languageService.validateSnippet(any(Snippet.class), any(LanguageVersion.class)))
        .thenReturn(new LanguageSuccess());
    when(snippetRepository.save(any(Snippet.class)))
        .thenAnswer(
            invocation -> {
              Snippet savedSnippet = invocation.getArgument(0);
              savedSnippet.setId(snippetId);
              return savedSnippet;
            });

    Snippet actualSnippet = snippetService.editSnippet(snippetId, snippetDto2);

    verify(blobStorageService).uploadSnippet(newUrl, "name2", "content2");

    assertEquals(finalSnippet, actualSnippet);
  }

  @Test
  void editSnippetNotFound() {
    Long snippetId = 1L;
    SnippetDto snippetDto = new SnippetDto("name", "content", userId, languageOk, versionOk);

    when(snippetRepository.findSnippetById(snippetId)).thenReturn(Optional.empty());

    assertThrows(
        NoSuchElementException.class, () -> snippetService.editSnippet(snippetId, snippetDto));
  }
}
