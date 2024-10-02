package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.clients.PermissionsClient;
import com.ingsis.jcli.snippets.common.PermissionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

  final PermissionsClient permissionsClient;
  final SnippetService snippetService;

  @Autowired
  public PermissionService(PermissionsClient permissionsClient, SnippetService snippetService) {
    this.permissionsClient = permissionsClient;
    this.snippetService = snippetService;
  }

  public boolean hasPermissionOnSnippet(PermissionType type, Long snippetId, Long userId) {
    if (snippetService.isOwner(snippetId, userId)) {
      return true;
    }

    ResponseEntity<Boolean> response = permissionsClient.hasPermission(type.name, snippetId, userId);
    if (response.getStatusCode().isError()) {
      // TODO
      return false;
    }
    return response.getBody() != null && response.getBody();
  }
}
