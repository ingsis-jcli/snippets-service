package com.ingsis.jcli.snippets.clients;

import com.ingsis.jcli.snippets.clients.factory.FeignException;
import com.ingsis.jcli.snippets.common.requests.ValidateRequest;
import com.ingsis.jcli.snippets.common.responses.ErrorResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "language")
public interface LanguageClient {

  @GetMapping("hello")
  String hello();

  @PostMapping(value = "/validate", consumes = "application/json", produces = "application/json")
  ResponseEntity<ErrorResponse> validate(@RequestBody ValidateRequest validateRequest)
      throws FeignException;
}
