package com.ingsis.jcli.snippets.common.exceptions;

public class SnippetNotFoundException extends RuntimeException {

  public SnippetNotFoundException(Long snippetId) {
    super("Snippet not found with id: " + snippetId);
  }
}
