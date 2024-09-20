package com.ingsis.jcli.snippets.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "permissions", url = "http://localhost:8081/")
public interface PermissionsClient {

  @RequestMapping(method = RequestMethod.GET, value = "/hello")
  String hello();
}
