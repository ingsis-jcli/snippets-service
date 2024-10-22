package com.ingsis.jcli.snippets.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ingsis.jcli.snippets.clients.LanguageClient;
import com.ingsis.jcli.snippets.clients.factory.LanguageClientFactory;
import com.ingsis.jcli.snippets.repositories.HelloRepository;
import com.ingsis.jcli.snippets.services.HelloService;
import java.time.Instant;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HelloController.class)
@ActiveProfiles("test")
class HelloControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private HelloService helloService;

  @MockBean private HelloRepository helloRepository;

  @MockBean private JwtDecoder jwtDecoder;

  @MockBean private LanguageClientFactory languageClientFactory;

  @MockBean private LanguageClient languageClient;

  private Jwt jwt;

  @BeforeEach
  void setup() {
    jwt =
        Jwt.withTokenValue("fake-token")
            .header("alg", "none")
            .claim("sub", "1234567890")
            .claim("scope", "read:snippets")
            .audience(Collections.singletonList("your-audience"))
            .issuer("https://your-auth0-domain/")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();

    Mockito.when(jwtDecoder.decode(Mockito.anyString())).thenReturn(jwt);
  }

  @Test
  void testGetHello() throws Exception {
    when(helloService.getHello()).thenReturn("Hello from snippets service!");

    mockMvc
        .perform(get("/hello").with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello from snippets service!"));
  }

  @Test
  void testGetHelloFromPrintScript() throws Exception {
    when(helloService.getHelloFromPrintScript()).thenReturn("Hello from printscript service!");

    mockMvc
        .perform(
            get("/hello/printscript").with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello from printscript service!"));
  }

  @Test
  void testGetHelloFromPermissions() throws Exception {
    when(helloService.getHelloFromPermissions()).thenReturn("Hello from permissions service!");

    mockMvc
        .perform(
            get("/hello/permissions").with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello from permissions service!"));
  }
}
