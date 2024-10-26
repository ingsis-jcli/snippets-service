package com.ingsis.jcli.snippets.producers;

import com.ingsis.jcli.snippets.models.TestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestCaseRunProducer extends JavaRedisStreamProducer {

  // TODO CONSUMER IN PRINTSCRIPT-SERVICE

  @Autowired
  public TestCaseRunProducer(
      @Value("${test_case_stream.key}") String streamKey, RedisTemplate<String, String> redis) {
    super(streamKey, redis);
  }

  public void run(TestCase testCase) {
    emit("Sending test case run for " + testCase.getId());
  }
}
