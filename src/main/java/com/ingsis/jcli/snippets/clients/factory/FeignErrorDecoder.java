package com.ingsis.jcli.snippets.clients.factory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ingsis.jcli.snippets.common.Generated;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;

@Generated
public class FeignErrorDecoder implements ErrorDecoder {

  @Override
  public Exception decode(String methodKey, Response response) {
    try {
      String bodyAsString =
          response.body() != null
              ? StreamUtils.copyToString(response.body().asInputStream(), StandardCharsets.UTF_8)
              : "";

      JsonObject json = new Gson().fromJson(bodyAsString, JsonObject.class);

      return new FeignException(ResponseEntity.status(response.status()).body(json));
    } catch (IOException e) {
      return new FeignException(ResponseEntity.status(response.status()).body(null));
    }
  }
}
