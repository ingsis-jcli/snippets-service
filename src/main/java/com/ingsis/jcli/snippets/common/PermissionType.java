package com.ingsis.jcli.snippets.common;

@Generated
public enum PermissionType {
  OWNER("owner"),
  READ("read"),
  WRITE("write"),
  EXECUTE("execute"),
  SHARE("share");

  public final String name;

  PermissionType(String name) {
    this.name = name;
  }
}
