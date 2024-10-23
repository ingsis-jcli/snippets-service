package com.ingsis.jcli.snippets.services;

import static com.ingsis.jcli.snippets.services.BlobStorageService.getBaseUrl;

import com.ingsis.jcli.snippets.common.exceptions.InvalidSnippetException;
import com.ingsis.jcli.snippets.common.language.LanguageResponse;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.repositories.SnippetRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SnippetService {

  final SnippetRepository snippetRepository;
  final BlobStorageService blobStorageService;
  final LanguageService languageService;

  @Autowired
  public SnippetService(
      SnippetRepository snippetRepository,
      BlobStorageService blobStorageService,
      LanguageService languageService) {
    this.snippetRepository = snippetRepository;
    this.blobStorageService = blobStorageService;
    this.languageService = languageService;
  }

  public void helloBucket() {
    blobStorageService.uploadSnippet("snippet", "hello.txt", "Hello Bucket");
  }

  public Optional<String> getSnippet(Long snippetId) {
    Optional<Snippet> snippetOptional = this.snippetRepository.findSnippetById(snippetId);
    if (snippetOptional.isPresent()) {
      Snippet snippet = snippetOptional.get();
      String name = snippet.getName();
      String url = snippet.getUrl();
      Optional<String> content = blobStorageService.getSnippet(url, name);
      return content;
    }
    return Optional.empty();
  }

  public Snippet createSnippet(SnippetDto snippetDto) {
    LanguageVersion languageVersion =
        languageService.getLanguageVersion(snippetDto.getLanguage(), snippetDto.getVersion());
    LanguageResponse isValid =
        languageService.validateSnippet(snippetDto.getContent(), languageVersion);

    if (isValid.hasError()) {
      throw new InvalidSnippetException(isValid.getError(), languageVersion);
    }

    blobStorageService.uploadSnippet(
        getBaseUrl(snippetDto), snippetDto.getName(), snippetDto.getContent());
    Snippet snippet =
        new Snippet(
            snippetDto.getName(), getBaseUrl(snippetDto), snippetDto.getOwner(), languageVersion);
    return snippetRepository.save(snippet);
  }

  public boolean isOwner(Long snippetId, String userId) {
    Optional<Snippet> snippet = this.snippetRepository.findSnippetById(snippetId);
    return snippet.filter(value -> userId.equals(value.getOwner())).isPresent();
  }

  public Snippet editSnippet(Long snippetId, SnippetDto snippetDto) {
    Optional<Snippet> snippet = this.snippetRepository.findSnippetById(snippetId);
    if (snippet.isEmpty()) {
      throw new NoSuchElementException("Snippet with id " + snippetId + " does not exist");
    }
    String languageName = snippetDto.getLanguage();
    String versionName = snippetDto.getVersion();
    LanguageVersion languageVersion = languageService.getLanguageVersion(languageName, versionName);
    LanguageResponse response =
        languageService.validateSnippet(snippetDto.getContent(), languageVersion);

    if (response.hasError()) {
      throw new InvalidSnippetException(response.getError(), languageVersion);
    }
    blobStorageService.deleteSnippet(snippet.get().getUrl(), snippet.get().getName());
    return createSnippet(snippetDto);
  }

  public List<SnippetDto> getAllSnippets(String userId) {
    List<Snippet> snippets = snippetRepository.findAllByOwner(userId);
    List<SnippetDto> snippetDtos = new ArrayList<>();
    for (Snippet snippet : snippets) {
      String content =
          blobStorageService.getSnippet(snippet.getUrl(), snippet.getName()).orElse("");
      SnippetDto dto =
          new SnippetDto(
              snippet.getName(),
              content,
              snippet.getOwner(),
              snippet.getLanguageVersion().getLanguage(),
              snippet.getLanguageVersion().getVersion());
      snippetDtos.add(dto);
    }
    return snippetDtos;
  }
}
