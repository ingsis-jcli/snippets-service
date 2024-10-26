package com.ingsis.jcli.snippets.producers;

import com.ingsis.jcli.snippets.models.TestCase;
import com.ingsis.jcli.snippets.producers.products.PendingTestCaseRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

@Component
public class TestCaseRunProducer extends JavaRedisStreamProducer {

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
    return template;
  }

  @Autowired
  public TestCaseRunProducer(
      @Value("${test_case_stream.key}") String streamKey, RedisTemplate<String, String> redis) {
    super(streamKey, redis);
  }

  public void run(TestCase testCase) {
    PendingTestCaseRun pendingTestCaseRun =
        new PendingTestCaseRun(
            testCase.getId(),
            testCase.getSnippet().getName(),
            testCase.getSnippet().getUrl(),
            testCase.getSnippet().getLanguageVersion().getVersion(),
            testCase.getInputs(),
            testCase.getOutputs());
    emit(pendingTestCaseRun);
  }
}
