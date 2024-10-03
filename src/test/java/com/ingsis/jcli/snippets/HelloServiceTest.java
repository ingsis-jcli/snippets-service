package com.ingsis.jcli.snippets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ingsis.jcli.snippets.clients.PermissionsClient;
import com.ingsis.jcli.snippets.clients.PrintScriptClient;
import com.ingsis.jcli.snippets.repositories.HelloRepository;
import com.ingsis.jcli.snippets.services.HelloService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class HelloServiceTest {

  @Autowired private HelloService helloService;

  @MockBean private HelloRepository helloRepository;

  @MockBean private PrintScriptClient printScriptClient;

  @MockBean private PermissionsClient permissionsClient;

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
