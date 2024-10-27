package com.ingsis.jcli.snippets.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Rule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull private String name;

  @NotNull private boolean isActive;

  @Column(name = "rule_value")
  private String value;

  public Rule(String name, boolean isActive, String value) {
    this.name = name;
    this.isActive = isActive;
    this.value = value;
  }

  public Rule(String name, boolean isActive, Number value) {
    this.name = name;
    this.isActive = isActive;
    this.value = value != null ? value.toString() : null;
  }

  public Number getNumericValue() {
    try {
      return value != null ? Double.parseDouble(value) : null;
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
