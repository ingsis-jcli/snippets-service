package com.ingsis.jcli.snippets.controllers;

import com.ingsis.jcli.snippets.common.PermissionType;
import com.ingsis.jcli.snippets.common.status.ProcessStatus;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.services.JwtService;
import com.ingsis.jcli.snippets.services.PermissionService;
import com.ingsis.jcli.snippets.services.SnippetService;
import com.ingsis.jcli.snippets.services.TestCaseService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/snippet")
public class SnippetController {

  private final SnippetService snippetService;
  private final PermissionService permissionService;
  private final TestCaseService testCaseService;
  private final JwtService jwtService;

  @Autowired
  public SnippetController(
      SnippetService snippetService,
      PermissionService permissionService,
      JwtService jwtService,
      TestCaseService testCaseService) {
    this.snippetService = snippetService;
    this.permissionService = permissionService;
    this.jwtService = jwtService;
    this.testCaseService = testCaseService;
  }

  @GetMapping()
  public ResponseEntity<String> getSnippet(
      @RequestParam Long snippetId, @RequestHeader("Authorization") String token) {

    String userId = jwtService.extractUserId(token);

    Optional<String> snippet = snippetService.getSnippetContent(snippetId);
    if (snippet.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    boolean hasPermission =
        permissionService.hasPermissionOnSnippet(PermissionType.READ, snippetId, userId);
    if (!hasPermission) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    return new ResponseEntity<>(snippet.get(), HttpStatus.OK);
  }

  @PostMapping()
  public ResponseEntity<Long> createSnippet(
      @RequestBody @Valid SnippetDto snippetDto, @RequestHeader("Authorization") String token) {

    String userId = jwtService.extractUserId(token);

    Snippet snippet = snippetService.createSnippet(snippetDto, userId);
    return new ResponseEntity<>(snippet.getId(), HttpStatus.CREATED);
  }

  @PutMapping()
  public ResponseEntity<Long> editSnippet(
      @RequestBody @Valid SnippetDto snippetDto,
      @RequestParam Long snippetId,
      @RequestHeader(name = "Authorization") String token) {

    String userId = jwtService.extractUserId(token);

    //    boolean hasPermission =
    //        permissionService.hasPermissionOnSnippet(PermissionType.WRITE, snippetId, userId);
    //    if (!hasPermission) {
    //      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    //    }

    Snippet snippet = snippetService.editSnippet(snippetId, snippetDto, userId);
    testCaseService.runAllTestCases(snippet);
    return new ResponseEntity<>(snippet.getId(), HttpStatus.OK);
  }

  @GetMapping("/search")
  public ResponseEntity<List<SnippetDto>> getSnippetsBy(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int pageSize,
      @RequestParam(value = "owner", defaultValue = "true") boolean isOwner,
      @RequestParam(value = "shared", defaultValue = "true") boolean isShared,
      @RequestParam("lintingStatus") Optional<ProcessStatus> lintingStatus,
      @RequestParam("name") Optional<String> name,
      @RequestParam("language") Optional<String> language,
      @RequestHeader("Authorization") String token) {

    String userId = jwtService.extractUserId(token);
    List<SnippetDto> snippets =
        snippetService.getSnippetsBy(
            userId, page, pageSize, isOwner, isShared, lintingStatus, name, language);

    return new ResponseEntity<>(snippets, HttpStatus.OK);
  }
}
