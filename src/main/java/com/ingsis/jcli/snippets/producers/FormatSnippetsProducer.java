package com.ingsis.jcli.snippets.producers;

import static com.ingsis.jcli.snippets.producers.factory.SerializerUtil.serializeFromLintOrFormatRequest;

import com.ingsis.jcli.snippets.models.Rule;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.producers.factory.JavaRedisStreamProducer;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
public class FormatSnippetsProducer extends JavaRedisStreamProducer {

  public FormatSnippetsProducer(String streamKey, RedisTemplate<String, String> redis) {
    super(streamKey, redis);
  }

  public void format(Snippet snippet, List<Rule> rules) {
    log.info(
        "Sending format request in stream key "
            + getStreamKey()
            + " for snippet "
            + snippet.getName());
    String message = serializeFromLintOrFormatRequest(rules, snippet);
    emit(message);
  }
}
