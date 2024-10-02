package com.ingsis.jcli.snippets.controllers;

import com.ingsis.jcli.snippets.common.PermissionType;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.services.PermissionService;
import com.ingsis.jcli.snippets.services.SnippetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

  @GetMapping()
  public ResponseEntity<Snippet> getSnippet(
      @RequestParam Long userId,
      @RequestParam Long snippetId) {

    Optional<Snippet> snippet = snippetService.getSnippet(snippetId);
    if (snippet.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    boolean hasPermission = permissionService.hasPermissionOnSnippet(PermissionType.READ, userId, snippetId);
    if (!hasPermission) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    return new ResponseEntity<>(snippet.get(), HttpStatus.OK);
  }

  @PostMapping("create")
  public ResponseEntity<Long> createSnippet(
      @RequestBody @Valid SnippetDto snippetDto) {

    Snippet snippet;
    try {
      snippet = snippetService.createSnippet(snippetDto);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(snippet.getId(), HttpStatus.CREATED);
  }
}
