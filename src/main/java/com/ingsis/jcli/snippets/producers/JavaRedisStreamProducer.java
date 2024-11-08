package com.ingsis.jcli.snippets.producers;

import com.ingsis.jcli.snippets.common.Generated;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;

@Generated
public abstract class JavaRedisStreamProducer {
  private final String streamKey;
  private final RedisTemplate<String, String> redis;

  protected JavaRedisStreamProducer(String streamKey, RedisTemplate<String, String> redis) {
    this.streamKey = streamKey;
    this.redis = redis;
  }

  public <T> RecordId emit(T value) {
    var record = StreamRecords.newRecord().ofObject(value).withStreamKey(streamKey);

    return redis.opsForStream().add(record);
  }

  public String getStreamKey() {
    return streamKey;
  }

  public RedisTemplate<String, String> getRedis() {
    return redis;
  }
}
