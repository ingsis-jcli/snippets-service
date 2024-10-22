package com.ingsis.jcli.snippets.controllers;

import com.ingsis.jcli.snippets.clients.LanguageClient;
import com.ingsis.jcli.snippets.clients.factory.LanguageClientFactory;
import com.ingsis.jcli.snippets.models.Hello;
import com.ingsis.jcli.snippets.repositories.HelloRepository;
import com.ingsis.jcli.snippets.services.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {
  private final HelloService helloService;
  private final HelloRepository helloRepository;
  private final LanguageClientFactory languageClientFactory;

  @Autowired
  public HelloController(
      HelloService helloService,
      HelloRepository helloRepository,
      LanguageClientFactory languageClientFactory) {
    this.helloService = helloService;
    this.helloRepository = helloRepository;
    this.languageClientFactory = languageClientFactory;
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

  @GetMapping("/printscript2")
  public ResponseEntity<String> getHelloFromPrintScript2() {
    LanguageClient client =
        languageClientFactory.createClient("http://infra-printscript-api:8080/");
    return new ResponseEntity<>(client.hello(), HttpStatus.OK);
  }
}
