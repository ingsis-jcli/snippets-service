package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.common.Generated;
import org.springframework.stereotype.Service;

@Generated
@Service
public class BlobStorageService {

  public String uploadSnippet(String content) {
    return content;
  }

  public String downloadSnippet(String url) {
    return url;
  }

  public String updateSnippet(String url, String content) {
    return url;
  }

  public String deleteSnippet(String url) {
    return "deleted";
  }
}
