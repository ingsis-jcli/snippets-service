package com.ingsis.jcli.snippets.services;

import static com.ingsis.jcli.snippets.services.BlobStorageService.getBaseUrl;

import com.ingsis.jcli.snippets.common.exceptions.InvalidSnippetException;
import com.ingsis.jcli.snippets.common.language.LanguageResponse;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.common.status.ProcessStatus;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import com.ingsis.jcli.snippets.models.Rule;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.producers.FormatSnippetsProducer;
import com.ingsis.jcli.snippets.producers.LintSnippetsProducer;
import com.ingsis.jcli.snippets.repositories.SnippetRepository;
import com.ingsis.jcli.snippets.specifications.SnippetSpecifications;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
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
  private final RulesService rulesService;
  private final LintSnippetsProducer lintSnippetsProducer;
  private final FormatSnippetsProducer formatSnippetsProducer;

  @Autowired
  public SnippetService(
      SnippetRepository snippetRepository,
      BlobStorageService blobStorageService,
      LanguageService languageService,
      PermissionService permissionService,
      RulesService rulesService,
      LintSnippetsProducer lintSnippetsProducer,
      FormatSnippetsProducer formatSnippetsProducer) {
    this.snippetRepository = snippetRepository;
    this.blobStorageService = blobStorageService;
    this.languageService = languageService;
    this.permissionService = permissionService;
    this.rulesService = rulesService;
    this.lintSnippetsProducer = lintSnippetsProducer;
    this.formatSnippetsProducer = formatSnippetsProducer;
  }

  public void helloBucket() {
    blobStorageService.uploadSnippet("snippet", "hello.txt", "Hello Bucket");
  }

  public Optional<String> getSnippetContent(Long snippetId) {
    Optional<Snippet> snippetOptional = this.snippetRepository.findSnippetById(snippetId);
    if (snippetOptional.isEmpty()) {
      return Optional.empty();
    }
    Snippet snippet = snippetOptional.get();
    String name = snippet.getName();
    String url = snippet.getUrl();
    Optional<String> content = blobStorageService.getSnippet(url, name);
    return content;
  }

  public Optional<Snippet> getSnippet(Long snippetId) {
    Optional<Snippet> snippetOptional = this.snippetRepository.findSnippetById(snippetId);
    return snippetOptional;
  }

  public Snippet createSnippet(SnippetDto snippetDto, String userId) {
    saveInBucket(snippetDto, userId);
    LanguageVersion languageVersion =
        languageService.getLanguageVersion(snippetDto.getLanguage(), snippetDto.getVersion());
    Snippet snippet = saveInDbTable(snippetDto, userId, languageVersion);
    validateSnippet(snippet, languageVersion);
    return snippet;
  }

  private void validateSnippet(Snippet snippet, LanguageVersion languageVersion) {
    LanguageResponse isValid = languageService.validateSnippet(snippet, languageVersion);

    if (isValid.hasError()) {
      throw new InvalidSnippetException(isValid.getError(), languageVersion);
    }
  }

  private @NotNull Snippet saveInDbTable(
      SnippetDto snippetDto, String userId, LanguageVersion languageVersion) {
    Snippet snippet =
        new Snippet(snippetDto.getName(), getBaseUrl(snippetDto, userId), userId, languageVersion);

    snippetRepository.save(snippet);
    return snippet;
  }

  private void saveInBucket(SnippetDto snippetDto, String userId) {
    blobStorageService.uploadSnippet(
        getBaseUrl(snippetDto, userId), snippetDto.getName(), snippetDto.getContent());
  }

  public boolean isOwner(Long snippetId, String userId) {
    Optional<Snippet> snippet = this.snippetRepository.findSnippetById(snippetId);
    return snippet.filter(value -> userId.equals(value.getOwner())).isPresent();
  }

  public Snippet editSnippet(Long snippetId, SnippetDto snippetDto, String userId) {
    Optional<Snippet> snippetOpt = this.snippetRepository.findSnippetById(snippetId);
    if (snippetOpt.isEmpty()) {
      throw new NoSuchElementException("Snippet with id " + snippetId + " does not exist");
    }

    Snippet snippet = snippetOpt.get();
    blobStorageService.deleteSnippet(snippet.getUrl(), snippet.getName());
    saveInBucket(snippetDto, userId);

    LanguageVersion languageVersion =
        languageService.getLanguageVersion(snippetDto.getLanguage(), snippetDto.getVersion());

    updateSnippetInDbTable(snippetDto, userId, snippet, languageVersion);

    validateSnippet(snippet, languageVersion);

    return snippet;
  }

  private void updateSnippetInDbTable(
      SnippetDto snippetDto, String userId, Snippet snippet, LanguageVersion languageVersion) {
    snippet.setName(snippetDto.getName());
    snippet.setUrl(getBaseUrl(snippetDto, userId));
    snippet.setLanguageVersion(languageVersion);
    snippetRepository.save(snippet);
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

  public List<SnippetDto> getSnippetsBy(
      String userId,
      int page,
      int pageSize,
      boolean isOwner,
      boolean isShared,
      Optional<ProcessStatus> lintingStatus,
      Optional<String> name,
      Optional<String> language) {

    Pageable pageable = PageRequest.of(page, pageSize);

    List<Specification<Snippet>> specs = new ArrayList<>();

    Specification<Snippet> spec =
        isOwner
            ? SnippetSpecifications.isOwner(userId)
            : Specification.not(SnippetSpecifications.isOwner(userId));
    specs.add(spec);

    List<Long> sharedWithUser = permissionService.getSnippetsSharedWithUser(userId);
    specs.add(SnippetSpecifications.isShared(sharedWithUser));
    spec =
        isShared
            ? SnippetSpecifications.isShared(sharedWithUser)
            : Specification.not(SnippetSpecifications.isShared(sharedWithUser));
    specs.add(spec);

    lintingStatus.ifPresent(
        status -> specs.add(SnippetSpecifications.snippetsLintingStatusIs(status)));

    name.ifPresent(match -> specs.add(SnippetSpecifications.nameHasWordThatStartsWith(match)));

    language.ifPresent(lang -> specs.add(SnippetSpecifications.isLanguage(lang)));

    Specification<Snippet> finalSpec = specs.stream().reduce(Specification::and).orElse(null);

    List<Snippet> snippets = snippetRepository.findAll(finalSpec, pageable).getContent();

    return snippets.stream().map(this::getSnippetDto).toList();
  }

  public void lintUserSnippets(String userId, LanguageVersion languageVersion) {
    List<Snippet> snippets = snippetRepository.findAllByOwner(userId);
    List<Rule> rules = rulesService.getLintingRules(userId, languageVersion);
    snippets.forEach(s -> lintSnippetsProducer.lint(s, rules));
  }

  public void formatUserSnippets(String userId, LanguageVersion languageVersion) {
    List<Snippet> snippets = snippetRepository.findAllByOwner(userId);
    List<Rule> rules = rulesService.getLintingRules(userId, languageVersion);
    snippets.forEach(s -> formatSnippetsProducer.format(s, rules));
  }
}
