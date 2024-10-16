package com.ingsis.jcli.snippets.clients;

import com.ingsis.jcli.snippets.common.language.LanguageResponse;
import com.ingsis.jcli.snippets.common.requests.ValidateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "language")
public interface LanguageClient {

  @PostMapping("validate")
  ResponseEntity<LanguageResponse> validate(@RequestBody ValidateRequest validateRequest);
}
