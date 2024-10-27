package com.ingsis.jcli.snippets.consumers;

import com.google.gson.Gson;
import com.ingsis.jcli.snippets.common.responses.SnippetStatusUpdateProduct;
import com.ingsis.jcli.snippets.common.responses.TestCaseResultProduct;

public class DeserializerUtil {
  public static TestCaseResultProduct deserializeIntoTestResult(String json) {
    Gson gson = new Gson();
    TestCaseResultProduct testCaseResultProduct = gson.fromJson(json, TestCaseResultProduct.class);
    return testCaseResultProduct;
  }

  public static SnippetStatusUpdateProduct deserializeIntoSnippetStatusUpdate(String json) {
    Gson gson = new Gson();
    SnippetStatusUpdateProduct snippetStatusUpdateProduct =
        gson.fromJson(json, SnippetStatusUpdateProduct.class);
    return snippetStatusUpdateProduct;
  }
}
