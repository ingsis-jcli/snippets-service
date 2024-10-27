package com.ingsis.jcli.snippets.producers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ingsis.jcli.snippets.models.Rule;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.models.TestCase;
import java.util.List;

public class SerializerUtil {
  public static String serializeFromTestCase(TestCase testCase, String version) {
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
    jsonObject.addProperty("version", version);
    return jsonObject.toString();
  }

  public static String serializeFromLintOrFormatRequest(List<Rule> rules, Snippet snippet) {
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
      if (rule.getValue() != null) {
        ruleObject.addProperty("value", rule.getValue());
      } else if (rule.getNumericValue() != null) {
        ruleObject.addProperty("value", rule.getNumericValue());
      }
      rulesArray.add(ruleObject);
    }
    jsonObject.add("rules", rulesArray);
    return jsonObject.toString();
  }

}
