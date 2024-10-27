package com.ingsis.jcli.snippets.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Rule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull private String name;

  @NotNull
  @JsonProperty("isActive")
  private boolean isActive;

  @Getter private String value;

  @Getter private Double numericValue;

  public Rule(String name, String value, boolean isActive) {
    this.name = name;
    this.isActive = isActive;
    this.value = value;
  }

  public Rule(String name, boolean isActive) {
    this.name = name;
    this.isActive = isActive;
  }

  public Rule(String name, Number numericValue, boolean isActive) {
    this.name = name;
    this.isActive = isActive;
    this.numericValue = numericValue != null ? numericValue.doubleValue() : null;
  }
}
