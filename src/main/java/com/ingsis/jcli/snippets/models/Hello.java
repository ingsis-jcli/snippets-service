package com.ingsis.jcli.snippets.models;

import com.ingsis.jcli.snippets.common.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

import java.time.LocalTime;

@Generated
@Entity
@Data // getters, setters, toString, equals, empty constructor
public class Hello {

  @SequenceGenerator(
      name ="hello",
      sequenceName = "hello_sequence",
      allocationSize = 10
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "hello")
  @Id
  private Long id;

  private LocalTime time;

  public Hello() {
    time = LocalTime.now();
  }
}
