package com.ingsis.jcli.snippets.services;

import static com.ingsis.jcli.snippets.services.BlobStorageService.getBaseUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ingsis.jcli.snippets.common.exceptions.InvalidSnippetException;
import com.ingsis.jcli.snippets.common.exceptions.SnippetNotFoundException;
import com.ingsis.jcli.snippets.common.language.LanguageError;
import com.ingsis.jcli.snippets.common.language.LanguageSuccess;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.common.status.ProcessStatus;
import com.ingsis.jcli.snippets.common.status.Status;
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

  @MockBean private PermissionService permissionService;

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

    Optional<String> result = snippetService.getSnippetContent(id);
    assertTrue(result.isPresent());
    assertEquals("Snippet content", result.get());
  }

  @Test
  void getSnippetNull() {
    Long id = 1L;

    when(snippetRepository.findSnippetById(id)).thenReturn(Optional.empty());
    Optional<String> result = snippetService.getSnippetContent(id);
    assertTrue(result.isEmpty());
  }

  @Test
  void createSnippetOk() {
    Long snippetId = 1L;
    String name = "name";
    String description = "description";
    String content = "content";
    String userId = "123";

    SnippetDto snippetDto = new SnippetDto(name, description, content, languageOk, versionOk);
    Snippet expected =
        new Snippet(name, description, getBaseUrl(snippetDto, userId), userId, languageVersionOk);
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

    Snippet actualSnippet = snippetService.createSnippet(snippetDto, userId);
    assertEquals(expected, actualSnippet);
    verify(blobStorageService).uploadSnippet(getBaseUrl(snippetDto, userId), name, content);
  }

  @Test
  void createSnippetException() {
    String name = "name";
    String content = "content";
    String errorMessage = "Invalid snippet error";

    SnippetDto snippetDto = new SnippetDto(name, content, languageOk, versionOk);
    Snippet snippet = new Snippet(name, "url", userId, languageVersionOk);

    when(languageService.getLanguageVersion(languageOk, versionOk)).thenReturn(languageVersionOk);

    when(languageService.validateSnippet(any(Snippet.class), any(LanguageVersion.class)))
        .thenReturn(new LanguageError(errorMessage));

    InvalidSnippetException exception =
        assertThrows(
            InvalidSnippetException.class, () -> snippetService.createSnippet(snippetDto, userId));

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

    SnippetDto snippetDto2 = new SnippetDto("name2", "content2", languageOk, versionOk);
    String newUrl = getBaseUrl(snippetDto2, userId);

    Snippet finalSnippet = new Snippet("name2", newUrl, "123", languageVersionOk);
    finalSnippet.setId(snippetId);

    when(snippetRepository.findSnippetById(snippetId)).thenReturn(Optional.of(initialSnippet));
    when(blobStorageService.getSnippet(initialUrl, initialName))
        .thenReturn(Optional.of("content1"));

    when(languageService.getLanguageVersion(languageOk, versionOk)).thenReturn(languageVersionOk);
    when(languageService.validateSnippet(any(Snippet.class), any(LanguageVersion.class)))
        .thenReturn(new LanguageSuccess());
    when(snippetRepository.save(any(Snippet.class))).thenReturn(finalSnippet);

    Snippet actualSnippet = snippetService.editSnippet(snippetId, snippetDto2, userId);

    verify(blobStorageService).deleteSnippet(initialUrl, initialName);
    verify(blobStorageService).uploadSnippet(newUrl, "name2", "content2");

    assertEquals(finalSnippet.getId(), actualSnippet.getId());
    assertEquals(finalSnippet.getName(), actualSnippet.getName());
    assertEquals(finalSnippet.getUrl(), actualSnippet.getUrl());
    assertEquals(finalSnippet.getOwner(), actualSnippet.getOwner());
    assertEquals(finalSnippet.getLanguageVersion(), actualSnippet.getLanguageVersion());
  }

  @Test
  void editSnippetNotFound() {
    Long snippetId = 1L;
    SnippetDto snippetDto = new SnippetDto("name", "content", languageOk, versionOk);

    when(snippetRepository.findSnippetById(snippetId)).thenReturn(Optional.empty());

    assertThrows(
        NoSuchElementException.class,
        () -> snippetService.editSnippet(snippetId, snippetDto, userId));
  }

  @Test
  void updateLintingStateNotFound() {
    Long snippetId = 1L;
    when(snippetRepository.findSnippetById(snippetId)).thenReturn(Optional.empty());

    assertThrows(
        SnippetNotFoundException.class,
        () -> snippetService.updateLintingStatus(ProcessStatus.COMPLIANT, snippetId));
  }

  @Test
  void updateFormattingStatusNotFound() {
    Long snippetId = 1L;
    when(snippetRepository.findSnippetById(snippetId)).thenReturn(Optional.empty());

    assertThrows(
        SnippetNotFoundException.class,
        () -> snippetService.updateFormattingStatus(ProcessStatus.COMPLIANT, snippetId));
  }

  @Test
  void updateLintingStatusNotFound() {
    Long snippetId = 1L;
    when(snippetRepository.findSnippetById(snippetId)).thenReturn(Optional.empty());

    assertThrows(
        SnippetNotFoundException.class,
        () -> snippetService.updateLintingStatus(ProcessStatus.COMPLIANT, snippetId));
  }

  @Test
  void updateFormattingStatusSuccess() {
    Snippet snippet = new Snippet();
    Status status = new Status();
    status.setFormatting(ProcessStatus.NON_COMPLIANT);
    status.setLinting(ProcessStatus.NON_COMPLIANT);
    snippet.setStatus(status);
    Long snippetId = 1L;
    snippet.setId(snippetId);
    when(snippetRepository.findSnippetById(snippetId)).thenReturn(Optional.of(snippet));
    snippetService.updateFormattingStatus(ProcessStatus.COMPLIANT, snippetId);
    assertEquals(ProcessStatus.COMPLIANT, snippet.getStatus().getFormatting());
  }

  @Test
  void updateLintingStatusSuccess() {
    Snippet snippet = new Snippet();
    Status status = new Status();
    status.setFormatting(ProcessStatus.NON_COMPLIANT);
    status.setLinting(ProcessStatus.NON_COMPLIANT);
    snippet.setStatus(status);
    Long snippetId = 1L;
    snippet.setId(snippetId);
    when(snippetRepository.findSnippetById(snippetId)).thenReturn(Optional.of(snippet));
    snippetService.updateLintingStatus(ProcessStatus.COMPLIANT, snippetId);
    assertEquals(ProcessStatus.COMPLIANT, snippet.getStatus().getLinting());
  }

  @Test
  void deleteSnippet() {
    Long snippetId = 1L;
    Snippet snippet = new Snippet();
    snippet.setOwner(userId);
    snippet.setId(snippetId);
    snippet.setUrl("http://example.com");
    snippet.setName("Test Snippet");

    when(snippetRepository.findSnippetById(snippetId)).thenReturn(Optional.of(snippet));
    snippetService.deleteSnippet(snippetId, userId);
    verify(blobStorageService).deleteSnippet(snippet.getUrl(), snippet.getName());
  }
}
