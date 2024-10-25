package com.ingsis.jcli.snippets.models;

import com.ingsis.jcli.snippets.common.requests.TestType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Entity
@Data
public class TestCase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "snippet_id")
  private Snippet snippet;

  @NotNull private String name;

  @ElementCollection @NotNull private List<String> inputs;

  @ElementCollection @NotNull private List<String> outputs;

  @NotNull private TestType type;

  public TestCase() {}

  public TestCase(
      Snippet snippet, String name, List<String> inputs, List<String> outputs, TestType type) {
    this.snippet = snippet;
    this.name = name;
    this.inputs = inputs;
    this.outputs = outputs;
    this.type = type;
  }
}
