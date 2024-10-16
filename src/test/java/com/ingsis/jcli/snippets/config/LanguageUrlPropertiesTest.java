package com.ingsis.jcli.snippets.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class LanguageUrlPropertiesTest {

  @Autowired private LanguageUrlProperties languageUrlProperties;

  @MockBean private JwtDecoder jwtDecoder;

  @Test
  public void getProperties() {
    assertEquals("http://printscript:8080/", languageUrlProperties.getPrintscript());
  }

  @Test
  public void setProperties() {
    String newUrl = "http://a:8080/";
    languageUrlProperties.setPrintscript(newUrl);
    assertEquals(newUrl, languageUrlProperties.getPrintscript());
  }
}
