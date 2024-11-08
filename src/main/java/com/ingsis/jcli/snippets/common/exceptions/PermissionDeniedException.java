package com.ingsis.jcli.snippets.common.exceptions;

public class PermissionDeniedException extends RuntimeException {

  public PermissionDeniedException(DeniedAction action) {
    super("User is not allowed to perform this action: " + action);
  }
}
