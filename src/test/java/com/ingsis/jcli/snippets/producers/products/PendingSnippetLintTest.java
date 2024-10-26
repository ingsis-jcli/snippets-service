package com.ingsis.jcli.snippets.producers.products;

import static org.assertj.core.api.Assertions.assertThat;

import com.ingsis.jcli.snippets.common.requests.RuleDto;
import java.util.List;
import org.junit.jupiter.api.Test;

class PendingSnippetLintTest {

  @Test
  void testPendingSnippetLintCreation() {
    Long snippetId = 1L;
    String name = "Snippet Name";
    String url = "http://example.com/snippet";
    String version = "1.0";
    List<RuleDto> rules =
        List.of(new RuleDto(true, "rule1", "value1"), new RuleDto(false, "rule2", "value2"));
    PendingSnippetLint pendingSnippetLint =
        new PendingSnippetLint(snippetId, name, url, rules, version);
    assertThat(pendingSnippetLint.snippetId()).isEqualTo(snippetId);
    assertThat(pendingSnippetLint.name()).isEqualTo(name);
    assertThat(pendingSnippetLint.url()).isEqualTo(url);
    assertThat(pendingSnippetLint.version()).isEqualTo(version);
    assertThat(pendingSnippetLint.rules()).isEqualTo(rules);
  }

  @Test
  void testPendingSnippetLintEmptyRules() {
    Long snippetId = 2L;
    String name = "Empty Rules Snippet";
    String url = "http://example.com/empty-rules-snippet";
    String version = "1.1";
    List<RuleDto> emptyRules = List.of();

    PendingSnippetLint pendingSnippetLint =
        new PendingSnippetLint(snippetId, name, url, emptyRules, version);
    assertThat(pendingSnippetLint.snippetId()).isEqualTo(snippetId);
    assertThat(pendingSnippetLint.name()).isEqualTo(name);
    assertThat(pendingSnippetLint.url()).isEqualTo(url);
    assertThat(pendingSnippetLint.version()).isEqualTo(version);
    assertThat(pendingSnippetLint.rules()).isEqualTo(emptyRules);
  }
}
