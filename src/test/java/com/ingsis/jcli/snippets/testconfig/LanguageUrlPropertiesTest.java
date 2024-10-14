package com.ingsis.jcli.snippets.testconfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ingsis.jcli.snippets.config.LanguageUrlProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class LanguageUrlPropertiesTest {

  @Autowired private LanguageUrlProperties languageUrlProperties;

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
