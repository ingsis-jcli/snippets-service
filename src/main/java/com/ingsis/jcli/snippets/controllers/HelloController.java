package com.ingsis.jcli.snippets.controllers;

import com.ingsis.jcli.snippets.models.Hello;
import com.ingsis.jcli.snippets.repositories.HelloRepository;
import com.ingsis.jcli.snippets.services.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {
  private final HelloService helloService;
  private final HelloRepository helloRepository;

  @Autowired
  public HelloController(HelloService helloService, HelloRepository helloRepository) {
    this.helloService = helloService;
    this.helloRepository = helloRepository;
  }

  @PostMapping("/create")
  public Hello createHello() {
    // Create a new Hello entity and save it to the database
    Hello hello = new Hello();
    helloRepository.save(hello);
    return hello;
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
