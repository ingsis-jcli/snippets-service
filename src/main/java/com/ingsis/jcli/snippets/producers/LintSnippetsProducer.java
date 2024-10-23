package com.ingsis.jcli.snippets.producers;

import com.ingsis.jcli.snippets.common.requests.LintRequest;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import com.ingsis.jcli.snippets.models.Rule;
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

  public void lint(SnippetDto snippetDto, List<Rule> rules) {
    emit(new LintRequest(snippetDto, rules));
  }
}
