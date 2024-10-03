package com.ingsis.jcli.snippets.models;

import com.ingsis.jcli.snippets.common.Generated;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import java.util.Set;
import lombok.Data;

@Generated
@Entity
@Data
public class Language {

  @SequenceGenerator(name = "language", sequenceName = "language_sequence")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "language")
  @Id
  private Long id;

  @Column(unique = true)
  private String name;

  @OneToMany(mappedBy = "language", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<Version> versions;
}
