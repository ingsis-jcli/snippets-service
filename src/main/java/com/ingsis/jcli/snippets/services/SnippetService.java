package com.ingsis.jcli.snippets.services;

import static com.ingsis.jcli.snippets.services.BlobStorageService.getBaseUrl;
import static com.ingsis.jcli.snippets.services.BlobStorageService.getTemporaryBaseUrl;

import com.ingsis.jcli.snippets.clients.PermissionsClient;
import com.ingsis.jcli.snippets.common.PermissionType;
import com.ingsis.jcli.snippets.common.exceptions.DeniedAction;
import com.ingsis.jcli.snippets.common.exceptions.InvalidSnippetException;
import com.ingsis.jcli.snippets.common.exceptions.PermissionDeniedException;
import com.ingsis.jcli.snippets.common.exceptions.SnippetNotFoundException;
import com.ingsis.jcli.snippets.common.language.LanguageResponse;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.common.requests.RuleDto;
import com.ingsis.jcli.snippets.common.responses.FormatResponse;
import com.ingsis.jcli.snippets.common.responses.SnippetResponse;
import com.ingsis.jcli.snippets.common.status.ProcessStatus;
import com.ingsis.jcli.snippets.common.status.Status;
import com.ingsis.jcli.snippets.dto.SearchResult;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import com.ingsis.jcli.snippets.models.Rule;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.producers.FormatSnippetsProducer;
import com.ingsis.jcli.snippets.producers.LintSnippetsProducer;
import com.ingsis.jcli.snippets.producers.factory.LanguageProducerFactory;
import com.ingsis.jcli.snippets.repositories.SnippetRepository;
import com.ingsis.jcli.snippets.specifications.SnippetSpecifications;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SnippetService {

  private final SnippetRepository snippetRepository;
  private final BlobStorageService blobStorageService;
  private final LanguageService languageService;
  private final PermissionService permissionService;
  private final RulesService rulesService;
  private final LanguageProducerFactory languageProducerFactory;
  private final PermissionsClient permissionsClient;

  @Autowired
  public SnippetService(
      SnippetRepository snippetRepository,
      BlobStorageService blobStorageService,
      LanguageService languageService,
      PermissionService permissionService,
      RulesService rulesService,
      LanguageProducerFactory languageProducerFactory,
      PermissionsClient permissionsClient) {
    this.snippetRepository = snippetRepository;
    this.blobStorageService = blobStorageService;
    this.languageService = languageService;
    this.permissionService = permissionService;
    this.rulesService = rulesService;
    this.languageProducerFactory = languageProducerFactory;
    this.permissionsClient = permissionsClient;
  }

  public Optional<Snippet> getSnippet(Long snippetId) {
    return snippetRepository.findSnippetById(snippetId);
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

  public SnippetResponse createSnippet(SnippetDto snippetDto, String userId) {
    if (!snippetRepository.findAllByNameAndOwner(snippetDto.getName(), userId).isEmpty()) {
      throw new InvalidSnippetException("Snippet with the same name already exists", null);
    }
    if (!snippetRepository
        .findAllByNameAndOwner(snippetDto.getName().replaceAll("[\\s-]+", ""), userId)
        .isEmpty()) {
      throw new InvalidSnippetException("Snippet with the same name already exists", null);
    }
    saveTemporaryInBucket(snippetDto, userId);
    LanguageVersion languageVersion =
        languageService.getLanguageVersion(snippetDto.getLanguage(), snippetDto.getVersion());

    try {
      validateSnippet(
          snippetDto.getName(), getTemporaryBaseUrl(snippetDto, userId), languageVersion);
      Snippet snippet = saveInDbTable(snippetDto, userId, languageVersion);
      saveInBucket(snippetDto, userId);
      permissionService.grantOwnerPermission(snippet.getId());
      ProcessStatus lintingStatus = lintSnippet(snippet, userId);
      snippet.getStatus().setLinting(lintingStatus);
      snippetRepository.save(snippet);
      return getSnippetResponse(snippet);
    } catch (InvalidSnippetException e) {
      throw e;
    }
  }

  private void validateSnippet(String name, String url, LanguageVersion languageVersion) {
    LanguageResponse isValid = languageService.validateSnippet(name, url, languageVersion);
    if (isValid.hasError()) {
      log.error("Snippet not valid");
      throw new InvalidSnippetException(isValid.getError(), languageVersion);
    }
  }

  private ProcessStatus lintSnippet(Snippet snippet, String userId) {
    List<Rule> rules = rulesService.getLintingRules(userId, snippet.getLanguageVersion());
    List<RuleDto> ruleDtos = rules.stream().map(RuleDto::of).toList();

    ProcessStatus formatResponse =
        languageService.lintSnippet(ruleDtos, snippet, snippet.getLanguageVersion());

    return formatResponse;
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
    log.info("Snippet saved in bucket: " + snippetDto.getContent());
  }

  private void saveTemporaryInBucket(SnippetDto snippetDto, String userId) {
    blobStorageService.uploadSnippet(
        getTemporaryBaseUrl(snippetDto, userId), snippetDto.getName(), snippetDto.getContent());
    log.info("Snippet saved te in bucket: " + snippetDto.getContent());
  }

  private void deleteSnippet(Snippet snippet) {
    blobStorageService.deleteSnippet(snippet.getUrl(), snippet.getName());
    snippetRepository.delete(snippet);
  }

  public void deleteSnippet(Long snippetId, String userId) {
    Optional<Snippet> snippetOpt = this.snippetRepository.findSnippetById(snippetId);
    if (snippetOpt.isEmpty()) {
      throw new NoSuchElementException("Snippet not found");
    }
    Snippet snippet = snippetOpt.get();
    if (!isOwner(snippet, userId)) {
      throw new PermissionDeniedException(DeniedAction.DELETE_SNIPPET);
    }
    permissionsClient.deletePermissionsBySnippetId(snippetId);
    deleteSnippet(snippet);
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

  public Snippet editSnippet(Long snippetId, String content, String userId) {
    log.debug("Editing snippet with id: " + snippetId);
    log.debug("Content: " + content);

    if (!canEditSnippet(snippetId, userId)) {
      throw new PermissionDeniedException(DeniedAction.EDIT_SNIPPET);
    }

    Snippet snippet = getSnippet(snippetId).get();

    SnippetDto newSnippetDto =
        new SnippetDto(
            snippet.getName(),
            snippet.getDescription(),
            content,
            snippet.getLanguageVersion().getLanguage(),
            snippet.getLanguageVersion().getVersion());

    saveTemporaryInBucket(newSnippetDto, userId);
    LanguageVersion languageVersion = snippet.getLanguageVersion();

    validateSnippet(snippet.getName(), getTemporaryBaseUrl(newSnippetDto, userId), languageVersion);

    blobStorageService.deleteSnippet(snippet.getUrl(), snippet.getName());
    saveInBucket(newSnippetDto, userId);

    ProcessStatus lintingStatus = lintSnippet(snippet, userId);
    snippet.getStatus().setLinting(lintingStatus);

    snippetRepository.save(snippet);
    return snippet;
  }

  public SnippetResponse getSnippetResponse(Snippet snippet) {
    return new SnippetResponse(
        snippet.getId(),
        snippet.getName(),
        blobStorageService.getSnippet(snippet.getUrl(), snippet.getName()).orElse(""),
        snippet.getLanguageVersion().getLanguage(),
        snippet.getLanguageVersion().getVersion(),
        languageService.getExtension(snippet.getLanguageVersion()),
        snippet.getStatus().getLinting(),
        snippet.getOwner());
  }

  public SnippetResponse getSnippetDto(Long snippetId) {
    Snippet snippet = getSnippet(snippetId).orElseThrow(NoSuchElementException::new);
    String content = blobStorageService.getSnippet(snippet.getUrl(), snippet.getName()).orElse("");
    return new SnippetResponse(
        snippet.getId(),
        snippet.getName(),
        content,
        snippet.getLanguageVersion().getLanguage(),
        snippet.getLanguageVersion().getVersion(),
        languageService.getExtension(snippet.getLanguageVersion()),
        snippet.getStatus().getLinting(),
        snippet.getOwner());
  }

  public SearchResult getSnippetsBy(
      String userId,
      int page,
      int pageSize,
      boolean isOwner,
      boolean isShared,
      Optional<ProcessStatus> lintingStatus,
      Optional<String> name,
      Optional<String> language,
      Optional<String> orderBy) {

    Sort sort = orderBy.map(Sort::by).orElse(Sort.unsorted());
    Pageable pageable = PageRequest.of(page, pageSize, sort);

    if (!isOwner && !isShared) {
      return new SearchResult(0, List.of());
    }

    /* Adding possible snippets */
    List<Specification<Snippet>> specsAvailableSnippets = new ArrayList<>();

    if (isOwner) {
      Specification<Snippet> specOwner = SnippetSpecifications.isOwner(userId);
      specsAvailableSnippets.add(specOwner);
    }

    if (isShared) {
      List<Long> sharedWithUser = permissionService.getSnippetsSharedWithUser(userId);
      Specification<Snippet> specShared = SnippetSpecifications.isShared(sharedWithUser);
      specsAvailableSnippets.add(specShared);
    }

    Specification<Snippet> specAvailableSnippets =
        specsAvailableSnippets.stream().reduce(Specification::or).get();

    /* Filtering snippets */
    List<Specification<Snippet>> specs = new ArrayList<>();

    specs.add(specAvailableSnippets);

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
    long count = snippetRepository.count(finalSpec);

    List<SnippetResponse> snippetResponses = new ArrayList<>();

    snippets.forEach(s -> snippetResponses.add(getSnippetResponse(s)));

    return new SearchResult(count, snippetResponses);
  }

  public long getSnippetCount() {
    return snippetRepository.count();
  }

  public void lintUserSnippets(String userId, LanguageVersion languageVersion) {
    List<Snippet> snippets = snippetRepository.findAllByOwner(userId);
    List<Rule> rules = rulesService.getLintingRules(userId, languageVersion);

    LintSnippetsProducer lintSnippetsProducer =
        languageProducerFactory.getLintProducer(languageVersion.getLanguage());

    snippets.forEach(
        s -> {
          updateLintingStatus(ProcessStatus.PENDING, s.getId());
          lintSnippetsProducer.lint(s, rules);
        });
  }

  public void formatUserSnippets(String userId, LanguageVersion languageVersion) {
    List<Snippet> snippets = snippetRepository.findAllByOwner(userId);
    List<Rule> rules = rulesService.getFormattingRules(userId, languageVersion);

    FormatSnippetsProducer formatSnippetsProducer =
        languageProducerFactory.getFormatProducer(languageVersion.getLanguage());

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
  }

  public FormatResponse formatSnippetFromUser(String userId, Snippet snippet) {
    List<Rule> rules = rulesService.getFormattingRules(userId, snippet.getLanguageVersion());
    List<RuleDto> ruleDtos = rules.stream().map(RuleDto::of).toList();

    FormatResponse formatResponse =
        languageService.formatSnippet(ruleDtos, snippet, snippet.getLanguageVersion());

    return formatResponse;
  }
}
