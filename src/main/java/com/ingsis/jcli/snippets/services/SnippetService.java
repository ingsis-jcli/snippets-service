package com.ingsis.jcli.snippets.services;

import static com.ingsis.jcli.snippets.services.BlobStorageService.getBaseUrl;

import com.ingsis.jcli.snippets.common.exceptions.InvalidSnippetException;
import com.ingsis.jcli.snippets.common.language.LanguageResponse;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.repositories.SnippetRepository;
import com.ingsis.jcli.snippets.specifications.SnippetSpecifications;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SnippetService {

  private final SnippetRepository snippetRepository;
  private final BlobStorageService blobStorageService;
  private final LanguageService languageService;
  private final PermissionService permissionService;

  @Autowired
  public SnippetService(
      SnippetRepository snippetRepository,
      BlobStorageService blobStorageService,
      LanguageService languageService,
      PermissionService permissionService) {
    this.snippetRepository = snippetRepository;
    this.blobStorageService = blobStorageService;
    this.languageService = languageService;
    this.permissionService = permissionService;
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
    return snippets.stream().map(this::getSnippetDto).toList();
  }

  public SnippetDto getSnippetDto(Snippet snippet) {
    String content = blobStorageService.getSnippet(snippet.getUrl(), snippet.getName()).orElse("");
    return new SnippetDto(
        snippet.getName(),
        content,
        snippet.getOwner(),
        snippet.getLanguageVersion().getLanguage(),
        snippet.getLanguageVersion().getVersion());
  }

  public List<SnippetDto> getSnippetBy(
      String userId,
      int page,
      int pageSize,
      boolean isOwner,
      boolean isShared,
      Optional<Boolean> isValid,
      Optional<String> name,
      Optional<String> language) {

    Pageable pageable = PageRequest.of(page, pageSize);

    List<Specification<Snippet>> specs = new ArrayList<>();

    Specification<Snippet> spec =
        isOwner
            ? SnippetSpecifications.isOwner(userId)
            : Specification.not(SnippetSpecifications.isOwner(userId));
    specs.add(spec);

    if (isShared) {
      List<Long> sharedWithUser = permissionService.getSnippetsSharedWithUser(userId);
      specs.add(SnippetSpecifications.isShared(sharedWithUser));
      spec =
          isShared
              ? SnippetSpecifications.isShared(sharedWithUser)
              : Specification.not(SnippetSpecifications.isShared(sharedWithUser));
      specs.add(spec);
    }

    // TODO: isValid

    name.ifPresent(match -> specs.add(SnippetSpecifications.nameHasWordThatStartsWith(match)));

    language.ifPresent(lang -> specs.add(SnippetSpecifications.isLanguage(lang)));

    Specification<Snippet> finalSpec = specs.stream().reduce(Specification::and).orElse(null);

    List<Snippet> snippets = snippetRepository.findAll(finalSpec, pageable).getContent();

    return snippets.stream().map(this::getSnippetDto).toList();
  }
}
