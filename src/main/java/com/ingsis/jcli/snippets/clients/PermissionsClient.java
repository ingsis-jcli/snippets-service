package com.ingsis.jcli.snippets.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "permissions", url = "http://infra-permissions-api:8080/")
public interface PermissionsClient {

  @RequestMapping(method = RequestMethod.GET, value = "/hello")
  String hello();

  @RequestMapping(method = RequestMethod.GET, value = "/permissions/")
  ResponseEntity<Boolean> hasPermission(
      @RequestParam("type") String type,
      @RequestParam("snippetId") Long snippetId,
      @RequestParam("userId") String userId);

  @RequestMapping(method = RequestMethod.POST, value = "/permissions/create")
  ResponseEntity<String> addSnippet(
      @RequestParam("snippetId") Long snippetId, @RequestParam("userId") String userId);
}
