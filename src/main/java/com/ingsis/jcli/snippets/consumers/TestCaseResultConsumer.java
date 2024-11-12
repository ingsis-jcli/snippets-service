package com.ingsis.jcli.snippets.consumers;

import static com.ingsis.jcli.snippets.consumers.DeserializerUtil.deserializeIntoTestResult;

import com.ingsis.jcli.snippets.common.Generated;
import com.ingsis.jcli.snippets.common.requests.TestState;
import com.ingsis.jcli.snippets.common.requests.TestType;
import com.ingsis.jcli.snippets.common.responses.TestCaseResultProduct;
import com.ingsis.jcli.snippets.models.TestCase;
import com.ingsis.jcli.snippets.services.TestCaseService;
import java.time.Duration;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.austral.ingsis.redis.RedisStreamConsumer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Generated
@Profile("!test")
@Slf4j
@Component
public class TestCaseResultConsumer extends RedisStreamConsumer<String> {

  private final TestCaseService testCaseService;

  @Autowired
  public TestCaseResultConsumer(
      @Value("${test_result_stream.key}") String streamKey,
      @Value("${test_result.groups.product}") String groupId,
      @NotNull RedisTemplate<String, String> redis,
      TestCaseService testCaseService) {
    super(streamKey, groupId, redis);
    this.testCaseService = testCaseService;
  }

  @NotNull
  @Override
  protected StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> options() {
    return StreamReceiver.StreamReceiverOptions.builder()
        .pollTimeout(Duration.ofMillis(10000))
        .targetType(String.class)
        .onErrorResume(
            e -> {
              log.error(
                  "(TestCaseResultConsumer) Error occurred while receiving data: {}",
                  e.getMessage());
              return Mono.empty();
            })
        .build();
  }

  @Override
  protected void onMessage(@NotNull ObjectRecord<String, String> objectRecord) {
    try {
      String testResult = objectRecord.getValue();
      TestCaseResultProduct testCaseProduct = deserializeIntoTestResult(testResult);

      Long id = testCaseProduct.getTestCaseId();
      TestCase testCase = testCaseService.getTestCase(id).orElseThrow(NoSuchElementException::new);
      TestType type = testCaseProduct.getType();

      testCaseService.updateTestCaseState(
          testCase, type == testCase.getType() ? TestState.SUCCESS : TestState.FAILURE);
    } catch (Exception e) {
      log.error("(TestCaseResultConsumer) Error processing message: {}", e.getMessage(), e);
    }
  }
}
