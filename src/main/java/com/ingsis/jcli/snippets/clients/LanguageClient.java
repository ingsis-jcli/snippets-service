package com.ingsis.jcli.snippets.clients;

import com.ingsis.jcli.snippets.common.language.LanguageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URI;

@FeignClient
public interface LanguageClient {

  @RequestMapping(method = RequestMethod.POST, value = "validate")
  LanguageResponse validate(URI baseUrl, String snippet, String version);
}
