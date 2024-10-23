package com.ingsis.jcli.snippets.producers;

import com.ingsis.jcli.snippets.common.requests.FormatRequest;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import com.ingsis.jcli.snippets.models.Rule;
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

  public void format(SnippetDto snippetDto, List<Rule> rules) {
    emit(new FormatRequest(snippetDto, rules));
  }
}
