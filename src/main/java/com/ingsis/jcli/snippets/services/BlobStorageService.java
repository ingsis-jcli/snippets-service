package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.clients.BucketRestClient;
import com.ingsis.jcli.snippets.clients.BucketRestTemplateFactory;
import com.ingsis.jcli.snippets.common.Generated;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Generated
@Service
public class BlobStorageService {

  private final BucketRestTemplateFactory bucketRestTemplateFactory;
  private final BucketRestClient bucketClient;

  @Autowired
  public BlobStorageService(BucketRestTemplateFactory bucketRestTemplateFactory) {
    this.bucketRestTemplateFactory = bucketRestTemplateFactory;
    this.bucketClient = bucketRestTemplateFactory.createClient();
  }

  public static String getBaseUrl(SnippetDto snippetDto, String userId) {
    return "snippets/" + snippetDto.getLanguage() + "-" + snippetDto.getVersion() + "-" + userId;
  }

  public void uploadSnippet(String container, String name, String content) {
    bucketClient.saveSnippet(container, name, content);
  }

  public Optional<String> getSnippet(String container, String name) {
    String snippet = bucketClient.getSnippet(container, name);
    return Optional.ofNullable(snippet);
  }

  public void deleteSnippet(String container, String name) {
    bucketClient.deleteSnippet(container, name);
  }
}
