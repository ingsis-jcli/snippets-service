package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.clients.BucketClient;
import com.ingsis.jcli.snippets.common.Generated;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Generated
@Service
public class BlobStorageService {

  private final BucketClient bucketClient;

  @Autowired
  public BlobStorageService(BucketClient bucketClient) {
    this.bucketClient = bucketClient;
  }

  public static String getBaseUrl(SnippetDto snippetDto) {
    return "snippets/"
        + snippetDto.getLanguage()
        + "-"
        + snippetDto.getVersion()
        + "-"
        + snippetDto.getOwner();
  }

  public void uploadSnippet(String container, String name, String content) {
    bucketClient.saveSnippet(container, name, content);
  }

  public Optional<String> getSnippet(String container, String name) {
    ResponseEntity<String> response = bucketClient.getSnippet(container, name);
    if (response.hasBody()) {
      return Optional.of(response.getBody());
    }
    return Optional.empty();
  }

  public void deleteSnippet(String container, String name) {
    bucketClient.deleteSnippet(container, name);
  }
}
