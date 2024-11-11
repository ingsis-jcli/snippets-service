package com.ingsis.jcli.snippets.clients;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "permissions", url = "http://infra-permissions-api:8080/")
public interface PermissionsClient {

  @RequestMapping(method = RequestMethod.GET, value = "/permissions")
  ResponseEntity<Boolean> hasPermission(
      @RequestParam("type") String type, @RequestParam("snippetId") Long snippetId);

  @RequestMapping(method = RequestMethod.POST, value = "permissions/own")
  ResponseEntity<Void> addSnippet(@RequestParam("snippetId") Long snippetId);

  @RequestMapping(method = RequestMethod.GET, value = "/permissions/shared")
  ResponseEntity<List<Long>> getSnippetsSharedWithUser();

  @RequestMapping(method = RequestMethod.DELETE, value = "/permissions/snippet/{snippetId}")
  ResponseEntity<Void> deletePermissionsBySnippetId(@PathVariable("snippetId") Long snippetId);
}
