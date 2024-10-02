package com.ingsis.jcli.snippets.services;

import org.springframework.stereotype.Service;

@Service
public class BlobStorageService {

  public String uploadSnippet(String content) {
    return content;
  }

  public String downloadSnippet(String url) {
    return url;
  }

  public String updateSnippet(String url, String snippet) {
    return url;
  }

  public String deleteSnippet(String url) {
    return "deleted";
  }
}
