package com.ingsis.jcli.snippets.clients.factory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ingsis.jcli.snippets.common.Generated;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;

@Generated
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

  private static final Marker marker = MarkerFactory.getMarker("FEIGN_ERROR_DECODER");

  @Override
  public Exception decode(String methodKey, Response response) {
    log.info(marker, "Decoding an error response");
    try {
      String bodyAsString =
          response.body() != null
              ? StreamUtils.copyToString(response.body().asInputStream(), StandardCharsets.UTF_8)
              : "";

      log.info(marker, "Response: " + bodyAsString);

      JsonObject json = new Gson().fromJson(bodyAsString, JsonObject.class);

      return new FeignException(ResponseEntity.status(response.status()).body(json));
    } catch (IOException e) {
      log.error(marker, "Error while decoding error response");
      log.error(marker, e.getMessage());
      return new FeignException(ResponseEntity.status(response.status()).body(null));
    }
  }
}
