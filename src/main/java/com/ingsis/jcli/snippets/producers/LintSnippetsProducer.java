package com.ingsis.jcli.snippets.producers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("snippetId", snippet.getId());
    jsonObject.addProperty("name", snippet.getName());
    jsonObject.addProperty("url", snippet.getUrl());
    jsonObject.addProperty("version", snippet.getLanguageVersion().getVersion());
    JsonArray rulesArray = new JsonArray();
    for (Rule rule : rules) {
      JsonObject ruleObject = new JsonObject();
      ruleObject.addProperty("isActive", rule.isActive());
      ruleObject.addProperty("name", rule.getName());
      ruleObject.addProperty("value", rule.getValue());
      rulesArray.add(ruleObject);
    }
    jsonObject.addProperty("rules", rulesArray.toString());
    System.out.println("Message emited: " + jsonObject.toString());
    emit(jsonObject.toString());
  }
}
