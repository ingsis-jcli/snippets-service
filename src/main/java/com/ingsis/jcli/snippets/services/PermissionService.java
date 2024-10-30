package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.clients.PermissionsClient;
import com.ingsis.jcli.snippets.common.PermissionType;
import java.util.List;
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

  public boolean hasPermissionOnSnippet(PermissionType type, Long snippetId) {
    ResponseEntity<Boolean> response = permissionsClient.hasPermission(type.name, snippetId);
    if (response == null || response.getStatusCode().isError()) {
      // TODO
      return false;
    }
    return response.getBody() != null && response.getBody();
  }

  public List<Long> getSnippetsSharedWithUser(String userId) { // token is added to request ?
    return permissionsClient.getSnippetsSharedWithUser().getBody();
  }

  public void grantOwnerPermission(Long snippetId) {
    permissionsClient.addSnippet(snippetId);
  }
}
