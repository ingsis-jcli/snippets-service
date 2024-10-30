package com.ingsis.jcli.snippets.services;

import static com.ingsis.jcli.snippets.services.BlobStorageService.getBaseUrl;

import com.ingsis.jcli.snippets.common.PermissionType;
import com.ingsis.jcli.snippets.common.exceptions.DeniedAction;
import com.ingsis.jcli.snippets.common.exceptions.InvalidSnippetException;
import com.ingsis.jcli.snippets.common.exceptions.PermissionDeniedException;
import com.ingsis.jcli.snippets.common.exceptions.SnippetNotFoundException;
import com.ingsis.jcli.snippets.common.language.LanguageResponse;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.common.status.ProcessStatus;
import com.ingsis.jcli.snippets.common.status.Status;
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

  public Optional<Snippet> getSnippet(Long snippetId) {
    Optional<Snippet> snippetOptional = this.snippetRepository.findSnippetById(snippetId);
    return snippetOptional;
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

  public Snippet createSnippet(SnippetDto snippetDto, String userId) {
    saveInBucket(snippetDto, userId);
    LanguageVersion languageVersion =
        languageService.getLanguageVersion(snippetDto.getLanguage(), snippetDto.getVersion());
    Snippet snippet = saveInDbTable(snippetDto, userId, languageVersion);
    try {
      validateSnippet(snippet, languageVersion);
    } catch (InvalidSnippetException e) {
      deleteSnippet(snippet);
      throw e;
    }
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
        new Snippet(
            snippetDto.getName(),
            snippetDto.getDescription(),
            getBaseUrl(snippetDto, userId),
            userId,
            languageVersion);

    snippetRepository.save(snippet);
    return snippet;
  }

  private void saveInBucket(SnippetDto snippetDto, String userId) {
    blobStorageService.uploadSnippet(
        getBaseUrl(snippetDto, userId), snippetDto.getName(), snippetDto.getContent());
  }

  private void deleteSnippet(Snippet snippet) {
    blobStorageService.deleteSnippet(snippet.getUrl(), snippet.getName());
    snippetRepository.delete(snippet);
  }

  public boolean isOwner(Snippet snippet, String userId) {
    return snippet.getOwner().equals(userId);
  }

  public boolean canGetSnippet(Long snippetId, String userId) {
    Optional<Snippet> snippetOpt = this.snippetRepository.findSnippetById(snippetId);
    if (snippetOpt.isEmpty()) {
      throw new NoSuchElementException("Snippet not found");
    }
    Snippet snippet = snippetOpt.get();
    if (isOwner(snippet, userId)) {
      return true;
    }
    return permissionService.hasPermissionOnSnippet(PermissionType.SHARED, snippetId);
  }

  public boolean canEditSnippet(Long snippetId, String userId) {
    Optional<Snippet> snippet = this.snippetRepository.findSnippetById(snippetId);
    if (snippet.isEmpty()) {
      throw new NoSuchElementException("Snippet not found");
    }
    return snippet.get().getOwner().equals(userId);
  }

  public Snippet editSnippet(Long snippetId, SnippetDto snippetDto, String userId) {
    boolean canEdit = canEditSnippet(snippetId, userId);
    if (!canEdit) {
      throw new PermissionDeniedException(DeniedAction.EDIT_SNIPPET);
    }

    Snippet snippet = getSnippet(snippetId).get();
    SnippetDto oldSnippetDto =
        new SnippetDto(
            snippet.getName(),
            snippet.getDescription(),
            blobStorageService.getSnippet(snippet.getUrl(), snippet.getName()).get(),
            snippet.getLanguageVersion().getLanguage(),
            snippet.getLanguageVersion().getVersion());

    blobStorageService.deleteSnippet(snippet.getUrl(), snippet.getName());
    saveInBucket(snippetDto, userId);
    LanguageVersion languageVersion =
        languageService.getLanguageVersion(snippetDto.getLanguage(), snippetDto.getVersion());
    updateSnippetInDbTable(snippetDto, userId, snippet, languageVersion);

    try {
      validateSnippet(snippet, languageVersion);
    } catch (InvalidSnippetException e) {
      deleteSnippet(snippet);
      createSnippet(oldSnippetDto, userId);
      throw e;
    }

    return snippet;
  }

  private void updateSnippetInDbTable(
      SnippetDto snippetDto, String userId, Snippet snippet, LanguageVersion languageVersion) {
    snippet.setName(snippetDto.getName());
    snippet.setUrl(getBaseUrl(snippetDto, userId));
    snippet.setLanguageVersion(languageVersion);
    snippetRepository.save(snippet);
  }

  public SnippetDto getSnippetDto(Snippet snippet) {
    String content = blobStorageService.getSnippet(snippet.getUrl(), snippet.getName()).orElse("");
    return new SnippetDto(
        snippet.getName(),
        snippet.getDescription(),
        content,
        snippet.getLanguageVersion().getLanguage(),
        snippet.getLanguageVersion().getVersion());
  }

  public SnippetDto getSnippetDto(Long snippetId) {
    Snippet snippet = getSnippet(snippetId).orElseThrow(NoSuchElementException::new);
    String content = blobStorageService.getSnippet(snippet.getUrl(), snippet.getName()).orElse("");
    return new SnippetDto(
        snippet.getName(),
        snippet.getDescription(),
        content,
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

    Specification<Snippet> specOwner = Specification.where(null);
    Specification<Snippet> specShared = Specification.where(null);

    if (isOwner) {
      specOwner = SnippetSpecifications.isOwner(userId);
    }

    if (isShared) {
      List<Long> sharedWithUser = permissionService.getSnippetsSharedWithUser(userId);
      specShared = SnippetSpecifications.isShared(sharedWithUser);
    }

    specs.add(Specification.where(specOwner).or(specShared));

    lintingStatus.ifPresent(
        status -> specs.add(SnippetSpecifications.snippetsLintingStatusIs(status)));

    name.ifPresent(match -> specs.add(SnippetSpecifications.nameHasWordThatStartsWith(match)));

    language.ifPresent(
        lang -> {
          languageService.validateLanguage(lang.toLowerCase());
          specs.add(SnippetSpecifications.isLanguage(lang));
        });

    Specification<Snippet> finalSpec =
        specs.stream().reduce(Specification::and).orElse(Specification.where(null));

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
    List<Rule> rules = rulesService.getFormattingRules(userId, languageVersion);
    snippets.forEach(s -> formatSnippetsProducer.format(s, rules));
  }

  public void updateLintingStatus(ProcessStatus processStatus, Long snippetId) {
    Optional<Snippet> optionalSnippet = getSnippet(snippetId);
    if (optionalSnippet.isEmpty()) {
      throw new SnippetNotFoundException(snippetId);
    }
    Snippet snippet = optionalSnippet.get();
    Status status = snippet.getStatus();
    status.setLinting(processStatus);
    snippetRepository.save(snippet);
    System.out.println("Snippet lint for " + snippet.getId() + " : " + snippet.getStatus());
  }

  public void updateFormattingStatus(ProcessStatus processStatus, Long snippetId) {
    Optional<Snippet> optionalSnippet = getSnippet(snippetId);
    if (optionalSnippet.isEmpty()) {
      throw new SnippetNotFoundException(snippetId);
    }
    Snippet snippet = optionalSnippet.get();
    Status status = snippet.getStatus();
    status.setFormatting(processStatus);
    snippetRepository.save(snippet);
    System.out.println("Snippet format for " + snippet.getId() + " : " + snippet.getStatus());
  }
}
