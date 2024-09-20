package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.clients.PermissionsClient;
import com.ingsis.jcli.snippets.clients.PrintScriptClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

  private final PrintScriptClient printScriptClient;
  private final PermissionsClient permissionsClient;

  @Autowired
  public HelloService(PrintScriptClient printScriptClient, PermissionsClient permissionsClient) {
    this.printScriptClient = printScriptClient;
    this.permissionsClient = permissionsClient;
  }

  public String getHello() {
    return "Hello from snippets service!";
  }

  public String getHelloFromPrintScript() {
    return printScriptClient.hello();
  }

  public String getHelloFromPermissions() {
    return permissionsClient.hello();
  }
}

