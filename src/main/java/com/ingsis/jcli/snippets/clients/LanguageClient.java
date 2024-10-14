package com.ingsis.jcli.snippets.clients;

import com.ingsis.jcli.snippets.common.language.LanguageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "language")
public interface LanguageClient {

  @RequestMapping(method = RequestMethod.POST, value = "validate")
  LanguageResponse validate(String snippet, String version);
}
