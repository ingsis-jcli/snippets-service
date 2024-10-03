package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.clients.PermissionsClient;
import com.ingsis.jcli.snippets.common.PermissionType;
import com.ingsis.jcli.snippets.models.Snippet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class PermissionServiceTest {

  @Autowired
  private PermissionService permissionService;

  @MockBean
  private SnippetService snippetService;

  @MockBean
  private PermissionsClient permissionsClient;

  @Test
  public void hasPermissionOwner() {
    Long snippetId = 1L;
    Long userId = 123L;
    Snippet snippet = new Snippet("name", "url", userId);
    snippet.setId(snippetId);

    when(snippetService.isOwner(snippetId, userId)).thenReturn(true);
    assertTrue(permissionService.hasPermissionOnSnippet(PermissionType.READ, snippetId, userId));
  }

  @Test
  public void hasPermissionOther() {
    Long snippetId = 1L;
    Long ownerId = 345L;
    Long userId = 123L;
    Snippet snippet = new Snippet("name", "url", ownerId);
    snippet.setId(snippetId);

    when(snippetService.isOwner(snippetId, userId)).thenReturn(false);
    when(permissionsClient.hasPermission(PermissionType.READ.name, snippetId, userId))
        .thenReturn(ResponseEntity.ok(true));

    assertTrue(permissionService.hasPermissionOnSnippet(PermissionType.READ, snippetId, userId));
    assertNotEquals(ownerId, userId);
  }

  @Test
  public void hasPermissionFalse() {
    Long snippetId = 1L;
    Long ownerId = 345L;
    Long userId = 123L;
    Snippet snippet = new Snippet("name", "url", ownerId);
    snippet.setId(snippetId);

    when(snippetService.isOwner(snippetId, userId)).thenReturn(false);
    when(permissionsClient.hasPermission(PermissionType.READ.name, snippetId, userId))
        .thenReturn(ResponseEntity.ok(false));

    assertFalse(permissionService.hasPermissionOnSnippet(PermissionType.READ, snippetId, userId));
    assertNotEquals(ownerId, userId);
  }
}
