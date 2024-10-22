package com.ingsis.jcli.snippets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ingsis.jcli.snippets.clients.PermissionsClient;
import com.ingsis.jcli.snippets.clients.PrintScriptClient;
import com.ingsis.jcli.snippets.repositories.HelloRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class HelloServiceTest {

  @Mock private HelloRepository helloRepository;

  @Mock private PrintScriptClient printScriptClient;

  @Mock private PermissionsClient permissionsClient;

  @InjectMocks private HelloService helloService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetHello() {
    String result = helloService.getHello();
    assertEquals("Hello from snippets service!", result);
  }

  @Test
  void testGetHelloFromPrintScript() {
    when(printScriptClient.hello()).thenReturn("Hello from printscript service!");
    String result = helloService.getHelloFromPrintScript();
    assertEquals("Hello from printscript service!", result);
    verify(printScriptClient).hello();
  }

  @Test
  void testGetHelloFromPermissions() {
    when(permissionsClient.hello()).thenReturn("Hello from permissions service!");
    String result = helloService.getHelloFromPermissions();
    assertEquals("Hello from permissions service!", result);
    verify(permissionsClient).hello();
  }
}
