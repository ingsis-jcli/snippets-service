package com.ingsis.jcli.snippets.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:languages-test.properties")
public class LanguageUrlPropertiesTest {

  @Autowired private LanguageProperties languageUrlProperties;

  @MockBean private JwtDecoder jwtDecoder;

  @Test
  public void getProperties() {
    Map<String, String> urls = languageUrlProperties.getUrls();
    assertEquals("${PRINTSCRIPT_URL}", urls.get("printscript")); // fix -test
  }
}
