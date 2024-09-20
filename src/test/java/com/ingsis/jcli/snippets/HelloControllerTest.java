package com.ingsis.jcli.snippets;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ingsis.jcli.snippets.controllers.HelloController;
import com.ingsis.jcli.snippets.services.HelloService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HelloController.class)
class HelloControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private HelloService helloService;

  @Test
  void testGetHello() throws Exception {
    when(helloService.getHello()).thenReturn("Hello from snippets service!");
    mockMvc
        .perform(get("/hello"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello from snippets service!"));
  }

  @Test
  void testGetHelloFromPrintScript() throws Exception {
    when(helloService.getHelloFromPrintScript()).thenReturn("Hello from printscript service!");
    mockMvc
        .perform(get("/hello/printscript"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello from printscript service!"));
  }

  @Test
  void testGetHelloFromPermissions() throws Exception {
    when(helloService.getHelloFromPermissions()).thenReturn("Hello from permissions service!");
    mockMvc
        .perform(get("/hello/permissions"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello from permissions service!"));
  }
}
