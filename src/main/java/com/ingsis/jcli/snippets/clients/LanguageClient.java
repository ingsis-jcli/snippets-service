package com.ingsis.jcli.snippets.clients;

import com.ingsis.jcli.snippets.clients.factory.FeignException;
import com.ingsis.jcli.snippets.common.requests.RuleDto;
import com.ingsis.jcli.snippets.common.requests.ValidateRequest;
import com.ingsis.jcli.snippets.common.responses.ErrorResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "language")
public interface LanguageClient {

  @GetMapping("hello")
  String hello();

  @PostMapping(value = "/validate", consumes = "application/json", produces = "application/json")
  ResponseEntity<ErrorResponse> validate(@RequestBody ValidateRequest validateRequest)
      throws FeignException;

  @GetMapping(
      value = "/formatting_rules",
      consumes = "application/json",
      produces = "application/json")
  ResponseEntity<List<RuleDto>> getFormattingRules(String version) throws FeignException;

  @GetMapping(value = "/linting_rules", produces = "application/json")
  ResponseEntity<List<RuleDto>> getLintingRules(@RequestParam("version") String version)
      throws FeignException;
}
