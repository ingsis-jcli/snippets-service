package com.ingsis.jcli.snippets.producers;

import com.ingsis.jcli.snippets.common.requests.RuleDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.producers.products.PendingSnippetLint;
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

  public void lint(Snippet snippet, List<RuleDto> rules) {
    emit(
        new PendingSnippetLint(
            snippet.getId(),
            snippet.getName(),
            snippet.getUrl(),
            rules,
            snippet.getLanguageVersion().getVersion()));
  }
}
