package com.ingsis.jcli.snippets.clients.factory;

import com.google.gson.JsonObject;
import lombok.Generated;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Generated
@Getter
public class FeignException extends Exception {
  private ResponseEntity<JsonObject> responseEntity;

  public FeignException(ResponseEntity<JsonObject> responseEntity) {
    this.responseEntity = responseEntity;
  }
}
