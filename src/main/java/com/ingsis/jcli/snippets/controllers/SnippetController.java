package com.ingsis.jcli.snippets.controllers;

import com.ingsis.jcli.snippets.common.SnippetFile;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.common.responses.FormatResponse;
import com.ingsis.jcli.snippets.common.responses.SnippetResponse;
import com.ingsis.jcli.snippets.common.status.ProcessStatus;
import com.ingsis.jcli.snippets.dto.SearchResult;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.services.JwtService;
import com.ingsis.jcli.snippets.services.LanguageService;
import com.ingsis.jcli.snippets.services.SnippetService;
import com.ingsis.jcli.snippets.services.TestCaseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

@Slf4j
@RestController
@RequestMapping("/snippet")
public class SnippetController {

  private final SnippetService snippetService;
  private final TestCaseService testCaseService;
  private final JwtService jwtService;
  private final LanguageService languageService;

  @Autowired
  public SnippetController(
      SnippetService snippetService,
      JwtService jwtService,
      TestCaseService testCaseService,
      LanguageService languageService) {
    this.snippetService = snippetService;
    this.jwtService = jwtService;
    this.testCaseService = testCaseService;
    this.languageService = languageService;
  }

  @GetMapping("/filetypes")
  public ResponseEntity<Map<String, String>> getFileTypes() {
    Map<LanguageVersion, String> extensions = languageService.getAllExtensions();
    Map<String, String> response =
        extensions.entrySet().stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.getKey().getLanguage() + ":" + entry.getKey().getVersion(),
                    Map.Entry::getValue));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping()
  public ResponseEntity<SnippetResponse> getSnippet(
      @RequestParam Long snippetId, @RequestHeader("Authorization") String token) {

    String userId = jwtService.extractUserId(token);

    SnippetResponse snippet = snippetService.getSnippetDto(snippetId);

    boolean hasPermission = snippetService.canGetSnippet(snippetId, userId);
    if (!hasPermission) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    return new ResponseEntity<>(snippet, HttpStatus.OK);
  }

  @PostMapping()
  public ResponseEntity<SnippetResponse> createSnippet(
      @RequestBody @Valid SnippetDto snippetDto, @RequestHeader("Authorization") String token) {

    String userId = jwtService.extractUserId(token);
    snippetDto.setVersion(snippetDto.getVersion());
    SnippetResponse snippet = snippetService.createSnippet(snippetDto, userId);
    return new ResponseEntity<>(snippet, HttpStatus.CREATED);
  }

  @PostMapping(value = "/upload", consumes = "multipart/form-data")
  public ResponseEntity<SnippetResponse> createSnippetFromFile(
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

    SnippetResponse snippet = snippetService.createSnippet(snippetDto, userId);
    return new ResponseEntity<>(snippet, HttpStatus.CREATED);
  }

  @PutMapping()
  public ResponseEntity<SnippetResponse> editSnippet(
      @RequestBody @Valid String content,
      @RequestParam("snippetId") Long snippetId,
      @RequestHeader(name = "Authorization") String token) {

    String userId = jwtService.extractUserId(token);

    Snippet snippet = snippetService.editSnippet(snippetId, content, userId);
    testCaseService.runAllTestCases(snippet);

    SnippetResponse snippetResponse = snippetService.getSnippetResponse(snippet);
    return new ResponseEntity<>(snippetResponse, HttpStatus.OK);
  }

  @PutMapping(value = "/upload", consumes = "multipart/form-data")
  public ResponseEntity<Long> editSnippetFromFile(
      @RequestParam Long snippetId,
      @RequestPart("file") MultipartFile file,
      @RequestHeader("Authorization") String token)
      throws IOException {

    String userId = jwtService.extractUserId(token);

    String content = new String(file.getBytes(), StandardCharsets.UTF_8);
    Snippet snippet = snippetService.editSnippet(snippetId, content, userId);
    testCaseService.runAllTestCases(snippet);
    return new ResponseEntity<>(snippet.getId(), HttpStatus.CREATED);
  }

  @GetMapping("/search")
  public ResponseEntity<SearchResult> getSnippetsBy(
      @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
      @RequestParam(value = "size", defaultValue = "10") @Min(1) int pageSize,
      @RequestParam(value = "owner", defaultValue = "true") boolean isOwner,
      @RequestParam(value = "shared", defaultValue = "true") boolean isShared,
      @RequestParam("lintingStatus") Optional<ProcessStatus> lintingStatus,
      @RequestParam("name") Optional<String> name,
      @RequestParam("language") Optional<String> language,
      @RequestParam(value = "orderBy") Optional<String> orderBy,
      @RequestHeader("Authorization") String token) {

    String userId = jwtService.extractUserId(token);
    SearchResult result =
        snippetService.getSnippetsBy(
            userId, page, pageSize, isOwner, isShared, lintingStatus, name, language, orderBy);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @GetMapping("/count")
  public ResponseEntity<Long> getSnippetCount() {
    Long count = snippetService.getSnippetCount();
    return new ResponseEntity<>(count, HttpStatus.OK);
  }

  @GetMapping("/download/{snippetId}")
  public ResponseEntity<Resource> downloadSnippet(
      @PathVariable Long snippetId,
      @RequestParam boolean formatted,
      @RequestHeader("Authorization") String token) {

    String userId = jwtService.extractUserId(token);

    boolean hasPermission = snippetService.canGetSnippet(snippetId, userId);
    if (!hasPermission) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    SnippetFile snippetFile = snippetService.getFileFromSnippet(snippetId, userId, formatted);

    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\""
                + formatName(snippetFile.filename())
                + "."
                + snippetFile.extension()
                + "\"")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(snippetFile.file());
  }

  private String formatName(String name) {
    return name.replaceAll("[\\s-]+", "");
  }

  @DeleteMapping("/{snippetId}")
  public void deleteSnippet(
      @PathVariable Long snippetId, @RequestHeader("Authorization") String token) {
    String userId = jwtService.extractUserId(token);
    snippetService.deleteSnippet(snippetId, userId);
  }

  @GetMapping("/format/{snippetId}")
  public ResponseEntity<String> formatSnippet(
      @PathVariable Long snippetId, @RequestHeader("Authorization") String token) {
    String userId = jwtService.extractUserId(token);

    FormatResponse formatResponse = snippetService.format(snippetId, userId);
    snippetService.editSnippet(snippetId, formatResponse.content(), userId);

    return ResponseEntity.ok(formatResponse.content());
  }
}
