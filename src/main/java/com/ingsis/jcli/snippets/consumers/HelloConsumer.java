package com.ingsis.jcli.snippets.consumers;

import lombok.extern.slf4j.Slf4j;
import org.austral.ingsis.redis.RedisStreamConsumer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class HelloConsumer extends RedisStreamConsumer<HelloCreated> {

  public HelloConsumer(
      @Value("${stream.key}") String streamKey,
      @Value("${groups.product}") String groupId,
      @NotNull RedisTemplate<String, String> redis) {
    super(streamKey, groupId, redis);
  }

  @NotNull
  @Override
  protected StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, HelloCreated>>
      options() {
    return StreamReceiver.StreamReceiverOptions.builder()
        .pollTimeout(Duration.ofMillis(10000))
        .targetType(HelloCreated.class)
        .build();
  }

  @Override
  protected void onMessage(@NotNull ObjectRecord<String, HelloCreated> objectRecord) {
    log.info("Message: " + objectRecord.getValue());
  }
}
