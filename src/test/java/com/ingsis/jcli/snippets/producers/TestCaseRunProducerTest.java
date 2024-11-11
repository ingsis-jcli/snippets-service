package com.ingsis.jcli.snippets.producers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.models.TestCase;
import com.ingsis.jcli.snippets.producers.factory.JavaRedisStreamProducer;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;

class TestCaseRunProducerTest {

  @Mock private RedisTemplate<String, String> redisTemplate;

  @Mock private JavaRedisStreamProducer javaRedisStreamProducer;

  @InjectMocks private TestCaseRunProducer testCaseRunProducer;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    testCaseRunProducer = new TestCaseRunProducer("testStream", redisTemplate);
  }

  @Test
  void testRun() {
    Snippet snippet = new Snippet();
    snippet.setName("Test Snippet");
    snippet.setUrl("http://example.com");

    TestCase testCase = new TestCase();
    testCase.setId(1L);
    testCase.setSnippet(snippet);
    testCase.setInputs(Arrays.asList("input1", "input2"));
    testCase.setOutputs(Arrays.asList("output1", "output2"));

    TestCaseRunProducer spyProducer = spy(testCaseRunProducer);
    RecordId mockRecordId = RecordId.of("1637846472718-0");
    doReturn(mockRecordId).when(spyProducer).emit(any());

    spyProducer.run(testCase, "1.1");

    verify(spyProducer, times(1)).emit(any());
  }
}
