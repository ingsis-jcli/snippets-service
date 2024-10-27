package com.ingsis.jcli.snippets.producers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ingsis.jcli.snippets.producers.factory.StreamProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class StreamPropertiesTest {

  @Autowired private StreamProperties streamProperties;
  @MockBean private JwtDecoder jwtDecoder;

  @Test
  void testLintingStreamProperties() {
    assertNotNull(streamProperties.getLinting(), "Linting properties should not be null");
    assertEquals("linting", streamProperties.getLinting().get("printscript"));
  }

  @Test
  void testFormattingStreamProperties() {
    assertNotNull(streamProperties.getFormatting(), "Formatting properties should not be null");
    assertEquals("formatting", streamProperties.getFormatting().get("printscript"));
  }

  @Test
  void testTestCaseStreamProperties() {
    assertNotNull(streamProperties.getTestcase(), "Testcase properties should not be null");
    assertEquals("testcase", streamProperties.getTestcase().get("printscript"));
  }
}
