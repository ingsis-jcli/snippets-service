package com.ingsis.jcli.snippets.producers.factory;

import com.ingsis.jcli.snippets.producers.FormatSnippetsProducer;
import com.ingsis.jcli.snippets.producers.LintSnippetsProducer;
import com.ingsis.jcli.snippets.producers.TestCaseRunProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class LanguageProducerFactory {

  private final StreamProperties streamProperties;
  private final RedisTemplate<String, String> redisTemplate;

  @Autowired
  public LanguageProducerFactory(
      StreamProperties streamProperties, RedisTemplate<String, String> redisTemplate) {
    this.streamProperties = streamProperties;
    this.redisTemplate = redisTemplate;
  }

  public LintSnippetsProducer getLintProducer(String language) {
    String streamKey =
        streamProperties
            .getLinting()
            .getOrDefault(language, streamProperties.getLinting().get("printscript"));
    return new LintSnippetsProducer(streamKey, redisTemplate);
  }

  public FormatSnippetsProducer getFormatProducer(String language) {
    String streamKey =
        streamProperties
            .getFormatting()
            .getOrDefault(language, streamProperties.getFormatting().get("printscript"));
    return new FormatSnippetsProducer(streamKey, redisTemplate);
  }

  public TestCaseRunProducer getTestCaseRunProducer(String language) {
    String streamKey =
        streamProperties
            .getTestcase()
            .getOrDefault(language, streamProperties.getTestcase().get("printscript"));
    return new TestCaseRunProducer(streamKey, redisTemplate);
  }
}
