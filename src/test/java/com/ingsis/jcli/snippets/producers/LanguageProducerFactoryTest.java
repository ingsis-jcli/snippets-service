package com.ingsis.jcli.snippets.producers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.ingsis.jcli.snippets.producers.factory.LanguageProducerFactory;
import com.ingsis.jcli.snippets.producers.factory.StreamProperties;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;

class LanguageProducerFactoryTest {

  @Mock private StreamProperties streamProperties;
  @Mock private RedisTemplate<String, String> redisTemplate;

  @InjectMocks private LanguageProducerFactory languageProducerFactory;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    Map<String, String> lintingStreams = new HashMap<>();
    lintingStreams.put("printscript", "printscript_linting_stream");
    lintingStreams.put("java", "java_linting_stream");

    Map<String, String> formattingStreams = new HashMap<>();
    formattingStreams.put("printscript", "printscript_formatting_stream");
    formattingStreams.put("java", "java_formatting_stream");

    Map<String, String> testCaseStreams = new HashMap<>();
    testCaseStreams.put("printscript", "printscript_testcase_stream");
    testCaseStreams.put("java", "java_testcase_stream");

    when(streamProperties.getLinting()).thenReturn(lintingStreams);
    when(streamProperties.getFormatting()).thenReturn(formattingStreams);
    when(streamProperties.getTestcase()).thenReturn(testCaseStreams);
  }

  @Test
  void testGetLintProducerWithValidLanguage() {
    LintSnippetsProducer lintProducer = languageProducerFactory.getLintProducer("java");
    assertEquals(
        "java_linting_stream",
        lintProducer.getStreamKey(),
        "Stream key should match the Java linting key");
  }

  @Test
  void testGetLintProducerWithDefaultLanguage() {
    LintSnippetsProducer lintProducer = languageProducerFactory.getLintProducer("unknown");
    assertEquals(
        "printscript_linting_stream",
        lintProducer.getStreamKey(),
        "Stream key should default to printscript linting key");
  }

  @Test
  void testGetFormatProducerWithValidLanguage() {
    FormatSnippetsProducer formatProducer = languageProducerFactory.getFormatProducer("java");
    assertEquals(
        "java_formatting_stream",
        formatProducer.getStreamKey(),
        "Stream key should match the Java formatting key");
  }

  @Test
  void testGetFormatProducerWithDefaultLanguage() {
    FormatSnippetsProducer formatProducer = languageProducerFactory.getFormatProducer("unknown");
    assertEquals(
        "printscript_formatting_stream",
        formatProducer.getStreamKey(),
        "Stream key should default to printscript formatting key");
  }

  @Test
  void testGetTestCaseRunProducerWithValidLanguage() {
    TestCaseRunProducer testCaseProducer = languageProducerFactory.getTestCaseRunProducer("java");
    assertEquals(
        "java_testcase_stream",
        testCaseProducer.getStreamKey(),
        "Stream key should match the Java test case key");
  }

  @Test
  void testGetTestCaseRunProducerWithDefaultLanguage() {
    TestCaseRunProducer testCaseProducer =
        languageProducerFactory.getTestCaseRunProducer("unknown");
    assertEquals(
        "printscript_testcase_stream",
        testCaseProducer.getStreamKey(),
        "Stream key should default to printscript test case key");
  }
}
