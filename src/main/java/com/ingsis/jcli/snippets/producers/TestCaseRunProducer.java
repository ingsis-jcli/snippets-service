package com.ingsis.jcli.snippets.producers;

import static com.ingsis.jcli.snippets.producers.factory.SerializerUtil.serializeFromTestCase;

import com.ingsis.jcli.snippets.models.TestCase;
import com.ingsis.jcli.snippets.producers.factory.JavaRedisStreamProducer;
import org.springframework.data.redis.core.RedisTemplate;

public class TestCaseRunProducer extends JavaRedisStreamProducer {

  public TestCaseRunProducer(String streamKey, RedisTemplate<String, String> redis) {
    super(streamKey, redis);
  }

  public void run(TestCase testCase, String version) {
    String message = serializeFromTestCase(testCase, version);
    System.out.println("Message emitted: " + message);
    emit(message);
  }
}
