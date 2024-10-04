package com.ingsis.jcli.snippets.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "printscript", url = "http://infra-printscript-api:8080/")
public interface PrintScriptClient {

  @RequestMapping(method = RequestMethod.GET, value = "/hello")
  String hello();
}
