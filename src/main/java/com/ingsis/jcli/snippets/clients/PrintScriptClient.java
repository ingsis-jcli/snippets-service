package com.ingsis.jcli.snippets.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "printscript", url = "http://printscript-service:8082/")
public interface PrintScriptClient {

  @RequestMapping(method = RequestMethod.GET, value = "/hello")
  String hello();
}
