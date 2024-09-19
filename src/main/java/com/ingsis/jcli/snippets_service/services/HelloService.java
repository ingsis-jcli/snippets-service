package com.ingsis.jcli.snippets_service.services;

import org.springframework.stereotype.Service;

@Service
public class HelloService {
  public String getHello() {
    return "Hello World!";
  }
}
