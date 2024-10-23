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
@Table(name = "formatting_rules")
public class FormattingRules {

  @Id private String userId;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "formatting_rules_id")
  private List<Rule> rules;

  public FormattingRules(String userId, List<Rule> rules) {
    this.userId = userId;
    this.rules = rules;
  }
}
