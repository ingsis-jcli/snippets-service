package com.ingsis.jcli.snippets.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "permissions", url = "http://permissions-service:8081/")
public interface PermissionsClient {

  @RequestMapping(method = RequestMethod.GET, value = "/hello")
  String hello();

  @RequestMapping(method = RequestMethod.GET, value = "/permissions/")
  ResponseEntity<Boolean> canReadSnippet(
      @RequestParam("snippetId") Long snippetId,
      @RequestParam("userId") Long userId);
}
