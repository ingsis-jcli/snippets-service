package com.ingsis.jcli.snippets.controllers;

import com.ingsis.jcli.snippets.services.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {
  private final HelloService helloService;

  @Autowired
  public HelloController(HelloService helloService) {
    this.helloService = helloService;
  }

  @GetMapping
  public String getHello() {
    return helloService.getHello();
  }

  @GetMapping("/printscript")
  public String getHelloFromPrintScript() {
    return helloService.getHelloFromPrintScript();
  }

  @GetMapping("/permissions")
  public String getHelloFromPermissions() {
    return helloService.getHelloFromPermissions();
  }
}
