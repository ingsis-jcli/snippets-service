package com.ingsis.jcli.snippets.models;

import com.ingsis.jcli.snippets.common.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

@Generated
@Entity
@Data
public class Version {
  @SequenceGenerator(
      name = "version",
      sequenceName = "version_sequence"
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "version"
  )
  @Id
  private Long id;

  private String version;

  @ManyToOne
  @JoinColumn(name = "language_id", nullable = false)
  private Language language;
}
