package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.clients.PermissionsClient;
import com.ingsis.jcli.snippets.clients.PrintScriptClient;
import com.ingsis.jcli.snippets.models.Hello;
import com.ingsis.jcli.snippets.repositories.HelloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

  private final HelloRepository helloRepository;
  private final PrintScriptClient printScriptClient;
  private final PermissionsClient permissionsClient;

  @Autowired
  public HelloService(
      HelloRepository helloRepository,
      PrintScriptClient printScriptClient,
      PermissionsClient permissionsClient) {
    this.helloRepository = helloRepository;
    this.printScriptClient = printScriptClient;
    this.permissionsClient = permissionsClient;
  }

  public String getHello() {
    return "Hello from snippets service!";
  }

  public String getHelloFromPrintScript() {
    String msg = printScriptClient.hello();
    helloRepository.save(new Hello());
    return msg;
  }

  public String getHelloFromPermissions() {
    String msg = permissionsClient.hello();
    helloRepository.save(new Hello());
    return msg;
  }
}
