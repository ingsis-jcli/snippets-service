package com.ingsis.jcli.snippets.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "linting_rules")
public class LintingRules {

  @Id private String userId;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "linting_rules_id")
  private List<Rule> rules;

  public LintingRules(String userId, List<Rule> rules) {
    this.userId = userId;
    this.rules = rules;
  }
}
