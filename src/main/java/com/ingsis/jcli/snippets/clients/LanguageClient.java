package com.ingsis.jcli.snippets.clients;

import com.ingsis.jcli.snippets.common.requests.ValidateRequest;
import feign.Headers;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "language")
public interface LanguageClient {

  @RequestLine("POST /validate")
  @Headers("Content-Type: application/json")
  ResponseEntity<String> validate(@RequestBody ValidateRequest validateRequest);
}
