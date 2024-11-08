package com.ingsis.jcli.snippets.producers;

import static com.ingsis.jcli.snippets.producers.SerializerUtil.serializeFromTestCase;

import com.ingsis.jcli.snippets.models.TestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestCaseRunProducer extends JavaRedisStreamProducer {

  @Autowired
  public TestCaseRunProducer(
      @Value("${test_case_stream.key}") String streamKey, RedisTemplate<String, String> redis) {
    super(streamKey, redis);
  }

  public void run(TestCase testCase, String version) {
    String message = serializeFromTestCase(testCase, version);
    System.out.println("Message emitted: " + message);
    emit(message);
  }
}
