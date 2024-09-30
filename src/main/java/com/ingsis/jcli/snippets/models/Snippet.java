package com.ingsis.jcli.snippets.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Snippet {

  @SequenceGenerator(
      name = "snippet",
      sequenceName = "snippet_sequence"
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "snippet"
  )
  @Id
  private Long id;

  private String name;

  private String url;

  private Long userId;
}
