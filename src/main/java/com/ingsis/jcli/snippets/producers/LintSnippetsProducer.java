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
public class LintSnippetsProducer extends JavaRedisStreamProducer {

  @Autowired
  public LintSnippetsProducer(
      @Value("${linting_stream.key}") String streamKey, RedisTemplate<String, String> redis) {
    super(streamKey, redis);
  }

  public void lint(Snippet snippet, List<Rule> rules) {
    String message = serializeFromLintOrFormatRequest(rules, snippet);
    System.out.println("Message emited for lint: " + message);
    emit(message);
  }
}
