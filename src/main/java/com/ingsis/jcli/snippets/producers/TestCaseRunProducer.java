package com.ingsis.jcli.snippets.producers;

import com.ingsis.jcli.snippets.models.TestCase;
import com.ingsis.jcli.snippets.producers.products.PendingTestCaseProduct;
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

  public void run(TestCase testCase) {
    PendingTestCaseProduct pendingTestCaseProduct =
        new PendingTestCaseProduct(
            testCase.getId(),
            testCase.getSnippet().getName(),
            testCase.getSnippet().getUrl(),
            testCase.getSnippet().getLanguageVersion().getVersion(),
            testCase.getInputs(),
            testCase.getOutputs());
    emit(pendingTestCaseProduct);
  }
}
