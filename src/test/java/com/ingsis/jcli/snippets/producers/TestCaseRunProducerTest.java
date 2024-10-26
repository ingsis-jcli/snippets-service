package com.ingsis.jcli.snippets.producers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.models.TestCase;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;

class TestCaseRunProducerTest {

  @Mock private RedisTemplate<String, Object> redisTemplate;

  @Mock private StreamOperations<String, Object, Object> streamOperations;

  @InjectMocks private TestCaseRunProducer testCaseRunProducer;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(redisTemplate.opsForStream()).thenReturn(streamOperations);
  }

  @Test
  void testRun() {
    Snippet snippet = new Snippet();
    snippet.setName("Test Snippet");
    snippet.setUrl("http://example.com");
    snippet.setLanguageVersion(new LanguageVersion("testLang", "1.0"));

    TestCase testCase = new TestCase();
    testCase.setId(1L);
    testCase.setSnippet(snippet);
    testCase.setInputs(Collections.singletonList("input"));
    testCase.setOutputs(Collections.singletonList("output"));

    testCaseRunProducer.run(testCase);

    verify(streamOperations).add(any(ObjectRecord.class));
  }
}
