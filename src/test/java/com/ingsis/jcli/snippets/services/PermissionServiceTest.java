package com.ingsis.jcli.snippets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import com.ingsis.jcli.snippets.clients.PermissionsClient;
import com.ingsis.jcli.snippets.common.PermissionType;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class PermissionServiceTest {

  @InjectMocks private PermissionService permissionService;

  @Mock private PermissionsClient permissionsClient;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void hasPermissionOnSnippet_WhenPermissionDenied_ReturnsFalse() {
    Long snippetId = 1L;
    PermissionType permissionType = PermissionType.OWNER;

    when(permissionsClient.hasPermission(permissionType.name(), snippetId))
        .thenReturn(new ResponseEntity<>(false, HttpStatus.OK));

    boolean hasPermission = permissionService.hasPermissionOnSnippet(permissionType, snippetId);

    assertFalse(hasPermission);
  }

  @Test
  void hasPermissionOnSnippet_WhenResponseIsError_ReturnsFalse() {
    Long snippetId = 1L;
    PermissionType permissionType = PermissionType.OWNER;

    when(permissionsClient.hasPermission(permissionType.name(), snippetId))
        .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

    boolean hasPermission = permissionService.hasPermissionOnSnippet(permissionType, snippetId);

    assertFalse(hasPermission);
  }

  @Test
  void getSnippetsSharedWithUser_ReturnsListOfSnippetIds() {
    String userId = "user123";
    List<Long> expectedSnippets = List.of(1L, 2L, 3L);

    when(permissionsClient.getSnippetsSharedWithUser())
        .thenReturn(new ResponseEntity<>(expectedSnippets, HttpStatus.OK));

    List<Long> sharedSnippets = permissionService.getSnippetsSharedWithUser(userId);

    assertEquals(expectedSnippets, sharedSnippets);
  }

  @Test
  void getSnippetsSharedWithUser_WhenResponseIsEmpty_ReturnsEmptyList() {
    String userId = "user123";

    when(permissionsClient.getSnippetsSharedWithUser())
        .thenReturn(new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK));

    List<Long> sharedSnippets = permissionService.getSnippetsSharedWithUser(userId);

    assertEquals(Collections.emptyList(), sharedSnippets);
  }

  @Test
  void grantOwnerPermission_CallsAddSnippetOnClient() {
    Long snippetId = 1L;
    permissionService.grantOwnerPermission(snippetId);
    when(permissionsClient.addSnippet(snippetId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
  }
}
