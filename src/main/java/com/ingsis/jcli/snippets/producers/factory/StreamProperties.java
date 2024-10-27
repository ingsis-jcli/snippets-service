package com.ingsis.jcli.snippets.producers.factory;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "streams")
public class StreamProperties {
  private Map<String, String> linting;
  private Map<String, String> formatting;
  private Map<String, String> testcase;

  public Map<String, String> getLinting() {
    return linting;
  }

  public void setLinting(Map<String, String> linting) {
    this.linting = linting;
  }

  public Map<String, String> getFormatting() {
    return formatting;
  }

  public void setFormatting(Map<String, String> formatting) {
    this.formatting = formatting;
  }

  public Map<String, String> getTestcase() {
    return testcase;
  }

  public void setTestcase(Map<String, String> testcase) {
    this.testcase = testcase;
  }
}
