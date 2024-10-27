package com.ingsis.jcli.snippets.producers;

import static com.ingsis.jcli.snippets.producers.SerializerUtil.serializeFromLintOrFormatRequest;

import com.ingsis.jcli.snippets.models.Rule;
import com.ingsis.jcli.snippets.models.Snippet;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class FormatSnippetsProducer extends JavaRedisStreamProducer {

  @Autowired
  public FormatSnippetsProducer(
      @Value("${formatting_stream.key}") String streamKey, RedisTemplate<String, String> redis) {
    super(streamKey, redis);
  }

  public void format(Snippet snippet, List<Rule> rules) {
    String message = serializeFromLintOrFormatRequest(rules, snippet);
    System.out.println("Message emited: " + message);
    emit(message);
  }
}
