package com.ingsis.jcli.snippets.producers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.models.Rule;
import com.ingsis.jcli.snippets.models.Snippet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;

public class LintSnippetsProducerTest {

  @Mock private RedisTemplate<String, String> redisTemplate;

  @Mock private JavaRedisStreamProducer javaRedisStreamProducer;

  @InjectMocks private LintSnippetsProducer lintSnippetsProducer;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    lintSnippetsProducer = new LintSnippetsProducer("testStream", redisTemplate);
  }

  @Test
  void testLint() {
    List<Rule> rules =
        List.of(
            new Rule("rule1", true, "value1"),
            new Rule("rule2", false, "value2"),
            new Rule("rule3", true, "value3"));

    Snippet snippet = new Snippet();
    snippet.setLanguageVersion(new LanguageVersion("printscript", "1.1"));

    LintSnippetsProducer spyProducer = spy(lintSnippetsProducer);

    RecordId mockRecordId = RecordId.of("1637846472718-0");
    doReturn(mockRecordId).when(spyProducer).emit(any());

    spyProducer.lint(snippet, rules);

    verify(spyProducer, times(1)).emit(any());
  }
}
