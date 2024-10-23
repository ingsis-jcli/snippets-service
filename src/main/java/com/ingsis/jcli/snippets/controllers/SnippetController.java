package com.ingsis.jcli.snippets.controllers;

import com.ingsis.jcli.snippets.common.PermissionType;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.services.PermissionService;
import com.ingsis.jcli.snippets.services.SnippetService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/snippet")
public class SnippetController {

  final SnippetService snippetService;
  final PermissionService permissionService;

  @Autowired
  public SnippetController(SnippetService snippetService, PermissionService permissionService) {
    this.snippetService = snippetService;
    this.permissionService = permissionService;
  }

  @PostMapping("/hello-bucket")
  public ResponseEntity<String> helloBucket() {
    snippetService.helloBucket();
    return new ResponseEntity<>("Hello Bucket", HttpStatus.OK);
  }

  @GetMapping()
  public ResponseEntity<String> getSnippet(
      @RequestParam String userId, @RequestParam Long snippetId) {

    Optional<String> snippet = snippetService.getSnippet(snippetId);
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
  public ResponseEntity<Long> createSnippet(@RequestBody @Valid SnippetDto snippetDto) {
    Snippet snippet = snippetService.createSnippet(snippetDto);
    return new ResponseEntity<>(snippet.getId(), HttpStatus.CREATED);
  }

  @PutMapping()
  public ResponseEntity<Long> editSnippet(
      @RequestBody @Valid SnippetDto snippetDto,
      @RequestParam String userId,
      @RequestParam Long snippetId) {

    boolean hasPermission =
        permissionService.hasPermissionOnSnippet(PermissionType.WRITE, snippetId, userId);
    if (!hasPermission) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    Snippet snippet = snippetService.editSnippet(snippetId, snippetDto);
    return new ResponseEntity<>(snippet.getId(), HttpStatus.OK);
  }
}
