package com.ingsis.jcli.snippets.clients;

import com.ingsis.jcli.snippets.common.language.LanguageResponse;
import com.ingsis.jcli.snippets.common.requests.ValidateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "language")
public interface LanguageClient {

  @RequestMapping(method = RequestMethod.POST, value = "validate")
  ResponseEntity<LanguageResponse> validate(@RequestBody ValidateRequest validateRequest);
}
