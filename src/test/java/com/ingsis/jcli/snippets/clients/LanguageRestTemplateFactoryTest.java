package com.ingsis.jcli.snippets.clients;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ingsis.jcli.snippets.clients.factory.LanguageRestTemplateFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class LanguageRestTemplateFactoryTest {

  @Mock private RestTemplate restTemplate;

  @InjectMocks private LanguageRestTemplateFactory factory;

  @BeforeEach
  void setUp() {
    factory = new LanguageRestTemplateFactory(restTemplate);
  }

  @Test
  void testCreateClient() {
    String baseUrl = "http://localhost:8080";
    LanguageRestClient client = factory.createClient(baseUrl);
    assertNotNull(client, "Client should not be null");
  }
}
