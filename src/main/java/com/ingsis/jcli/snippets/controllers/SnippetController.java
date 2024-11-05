package com.ingsis.jcli.snippets.controllers;

import com.ingsis.jcli.snippets.common.requests.RuleDto;
import com.ingsis.jcli.snippets.common.responses.FormatResponse;
import com.ingsis.jcli.snippets.common.status.ProcessStatus;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import com.ingsis.jcli.snippets.models.Rule;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.services.JwtService;
import com.ingsis.jcli.snippets.services.LanguageService;
import com.ingsis.jcli.snippets.services.RulesService;
import com.ingsis.jcli.snippets.services.SnippetService;
import com.ingsis.jcli.snippets.services.TestCaseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/snippet")
public class SnippetController {

  private final SnippetService snippetService;
  private final TestCaseService testCaseService;
  private final JwtService jwtService;
  private final RulesService rulesService;
  private final LanguageService languageService;

  @Autowired
  public SnippetController(
      SnippetService snippetService,
      JwtService jwtService,
      TestCaseService testCaseService,
      RulesService rulesService,
      LanguageService languageService) {
    this.snippetService = snippetService;
    this.jwtService = jwtService;
    this.testCaseService = testCaseService;
    this.rulesService = rulesService;
    this.languageService = languageService;
  }

  @GetMapping("/filetypes")
  public ResponseEntity<Map<String, String>> getFileTypes() {
    return new ResponseEntity<>(languageService.getAllExtensions(), HttpStatus.OK);
  }

  @GetMapping()
  public ResponseEntity<SnippetDto> getSnippet(
      @RequestParam Long snippetId, @RequestHeader("Authorization") String token) {

    String userId = jwtService.extractUserId(token);

    SnippetDto snippet = snippetService.getSnippetDto(snippetId);

    boolean hasPermission = snippetService.canGetSnippet(snippetId, userId);
    if (!hasPermission) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    return new ResponseEntity<>(snippet, HttpStatus.OK);
  }

  @PostMapping()
  public ResponseEntity<Long> createSnippet(
      @RequestBody @Valid SnippetDto snippetDto,
      @RequestParam(value = "version", defaultValue = "1.1") String version,
      @RequestHeader("Authorization") String token) {

    String userId = jwtService.extractUserId(token);
    snippetDto.setVersion(version);

    Snippet snippet = snippetService.createSnippet(snippetDto, userId);
    return new ResponseEntity<>(snippet.getId(), HttpStatus.CREATED);
  }

  @PostMapping(value = "/upload", consumes = "multipart/form-data")
  public ResponseEntity<Long> createSnippetFromFile(
      @RequestParam String name,
      @RequestParam(required = false, defaultValue = "") String description,
      @RequestParam String language,
      @RequestParam String version,
      @RequestPart("file") MultipartFile file,
      @RequestHeader("Authorization") String token)
      throws IOException {

    String userId = jwtService.extractUserId(token);

    String content = new String(file.getBytes(), StandardCharsets.UTF_8);
    SnippetDto snippetDto = new SnippetDto(name, description, content, language, version);

    Snippet snippet = snippetService.createSnippet(snippetDto, userId);
    return new ResponseEntity<>(snippet.getId(), HttpStatus.CREATED);
  }

  @PutMapping()
  public ResponseEntity<Long> editSnippet(
      @RequestBody @Valid SnippetDto snippetDto,
      @RequestParam Long snippetId,
      @RequestHeader(name = "Authorization") String token) {

    String userId = jwtService.extractUserId(token);

    Snippet snippet = snippetService.editSnippet(snippetId, snippetDto, userId);
    testCaseService.runAllTestCases(snippet);
    return new ResponseEntity<>(snippet.getId(), HttpStatus.OK);
  }

  @PutMapping(value = "/upload", consumes = "multipart/form-data")
  public ResponseEntity<Long> editSnippetFromFile(
      @RequestParam Long snippetId,
      @RequestParam(required = false, defaultValue = "") String description,
      @RequestParam String name,
      @RequestParam String language,
      @RequestParam String version,
      @RequestPart("file") MultipartFile file,
      @RequestHeader("Authorization") String token)
      throws IOException {

    String userId = jwtService.extractUserId(token);

    String content = new String(file.getBytes(), StandardCharsets.UTF_8);
    SnippetDto snippetDto = new SnippetDto(name, description, content, language, version);

    Snippet snippet = snippetService.editSnippet(snippetId, snippetDto, userId);
    testCaseService.runAllTestCases(snippet);
    return new ResponseEntity<>(snippet.getId(), HttpStatus.CREATED);
  }

  @GetMapping("/search")
  public ResponseEntity<List<SnippetDto>> getSnippetsBy(
      @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
      @RequestParam(value = "size", defaultValue = "10") @Min(1) int pageSize,
      @RequestParam(value = "owner", defaultValue = "true") boolean isOwner,
      @RequestParam(value = "shared", defaultValue = "true") boolean isShared,
      @RequestParam("lintingStatus") Optional<ProcessStatus> lintingStatus,
      @RequestParam("name") Optional<String> name,
      @RequestParam("language") Optional<String> language,
      @RequestHeader("Authorization") String token) {
    // TODO: orderBy

    String userId = jwtService.extractUserId(token);
    List<SnippetDto> snippets =
        snippetService.getSnippetsBy(
            userId, page, pageSize, isOwner, isShared, lintingStatus, name, language);

    return new ResponseEntity<>(snippets, HttpStatus.OK);
  }

  @GetMapping("/download/{snippetId}")
  public ResponseEntity<Resource> downloadSnippet(
      @PathVariable Long snippetId,
      @RequestParam boolean formatted,
      @RequestHeader("Authorization") String token) {

    String userId = jwtService.extractUserId(token);

    Optional<Snippet> snippetOpt = snippetService.getSnippet(snippetId);
    if (snippetOpt.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Snippet snippet = snippetOpt.get();

    boolean hasPermission = snippetService.canGetSnippet(snippetId, userId);
    if (!hasPermission) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    Resource file;
    if (formatted) {
      FormatResponse formatResponse = formatSnippetFromUser(userId, snippet);
      file = new ByteArrayResource(formatResponse.content().getBytes(StandardCharsets.UTF_8));
    } else {
      Optional<String> snippetContent = snippetService.getSnippetContent(snippetId);
      if (snippetContent.isEmpty()) {
        return ResponseEntity.notFound().build();
      }
      file = new ByteArrayResource(snippetContent.get().getBytes(StandardCharsets.UTF_8));
    }

    String language = snippet.getLanguageVersion().getLanguage();

    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\""
                + snippet.getName()
                + "."
                + languageService.getExtension(language)
                + "\"")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(file);
  }

  private FormatResponse formatSnippetFromUser(String userId, Snippet snippet) {
    List<Rule> rules = rulesService.getFormattingRules(userId, snippet.getLanguageVersion());
    List<RuleDto> ruleDtos = rules.stream().map(RuleDto::of).toList();
    FormatResponse formatResponse =
        languageService.formatSnippet(ruleDtos, snippet, snippet.getLanguageVersion());
    return formatResponse;
  }

  @DeleteMapping("/{snippetId}")
  public void deleteSnippet(
      @PathVariable Long snippetId, @RequestHeader("Authorization") String token) {
    String userId = jwtService.extractUserId(token);
    snippetService.deleteSnippet(snippetId, userId);
  }
}
