package com.ingsis.jcli.snippets.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "bucket", url = "http://asset_service/v1")
public interface BucketClient {

  @GetMapping("/asset/{container}/{key}")
  ResponseEntity<String> getSnippet(
      @PathVariable("container") String container, @PathVariable("key") String key);

  @PutMapping("/asset/{container}/{key}")
  ResponseEntity<Void> saveSnippet(
      @PathVariable("container") String container,
      @PathVariable("key") String key,
      @RequestBody String content);

  @DeleteMapping("/asset/{container}/{key}")
  ResponseEntity<Void> deleteSnippet(
      @PathVariable("container") String container, @PathVariable("key") String key);
}
