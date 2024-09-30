package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.clients.PermissionsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

  final PermissionsClient permissionsClient;

  @Autowired
  public PermissionService(PermissionsClient permissionsClient) {
    this.permissionsClient = permissionsClient;
  }

  public boolean canReadSnippet(Long snippetId, Long userId) {
    ResponseEntity<Boolean> response = permissionsClient.canReadSnippet(snippetId, userId);
    if (response.getStatusCode().isError()) {
      // TODO
      return false;
    }
    return response.getBody() != null && response.getBody();
  }
}
