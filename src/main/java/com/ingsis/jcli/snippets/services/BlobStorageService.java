package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.clients.BucketClient;
import com.ingsis.jcli.snippets.common.Generated;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Generated
@Service
public class BlobStorageService {

  private final BucketClient bucketClient;

  @Autowired
  public BlobStorageService(BucketClient bucketClient) {
    this.bucketClient = bucketClient;
  }

  public static String getBaseUrl(SnippetDto snippetDto, String userId) {
    String formattedUserId = userId.replace("|", " ");
    String encodedUserId = URLEncoder.encode(formattedUserId, StandardCharsets.UTF_8);
    return "snippets/"
        + snippetDto.getLanguage()
        + "-"
        + snippetDto.getVersion()
        + "-"
        + encodedUserId;
  }

  public void uploadSnippet(String container, String name, String content) {
    bucketClient.saveSnippet(container, formatName(name), content);
  }

  public Optional<String> getSnippet(String container, String name) {
    ResponseEntity<String> response = bucketClient.getSnippet(container, formatName(name));
    if (response.hasBody()) {
      String body = response.getBody();
      return Optional.ofNullable(body);
    }
    return Optional.empty();
  }

  public void deleteSnippet(String container, String name) {
    bucketClient.deleteSnippet(container, formatName(name));
  }

  private String formatName(String name) {
    return URLEncoder.encode(name, StandardCharsets.UTF_8);
  }
}
