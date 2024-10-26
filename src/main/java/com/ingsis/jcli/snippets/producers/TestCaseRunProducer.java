package com.ingsis.jcli.snippets.producers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ingsis.jcli.snippets.models.TestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestCaseRunProducer extends JavaRedisStreamProducer {

  // TODO CONSUMER IN PRINTSCRIPT-SERVICE

  @Autowired
  public TestCaseRunProducer(
      @Value("${test_case_stream.key}") String streamKey, RedisTemplate<String, String> redis) {
    super(streamKey, redis);
  }

  public void run(TestCase testCase) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("id", testCase.getId());
    jsonObject.addProperty("snippetName", testCase.getSnippet().getName());
    jsonObject.addProperty("url", testCase.getSnippet().getUrl());
    JsonArray inputArray = new JsonArray();
    for (String input : testCase.getInputs()) {
      inputArray.add(input);
    }
    jsonObject.add("input", inputArray);
    JsonArray outputArray = new JsonArray();
    for (String output : testCase.getOutputs()) {
      outputArray.add(output);
    }
    jsonObject.add("output", outputArray);
    System.out.println(jsonObject);
    emit(jsonObject.toString());

  }
}
