package com.ingsis.jcli.snippets.clients.factory;

import com.ingsis.jcli.snippets.common.Generated;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StreamUtils;

@Generated
@Slf4j
public class FeignDecoder implements Decoder {

  private static final Marker marker = MarkerFactory.getMarker("FEIGN_DECODER");

  private final SpringDecoder springDecoder;

  public FeignDecoder() {
    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
    messageConverters.add(new MappingJackson2HttpMessageConverter());

    ObjectFactory<HttpMessageConverters> messageConvertersObjectFactory =
        () -> new HttpMessageConverters(messageConverters);

    this.springDecoder = new SpringDecoder(messageConvertersObjectFactory);
  }

  @Override
  public Object decode(Response response, Type type) throws IOException, DecodeException {
    log.info(marker, "Decoding response");
    if (response.body() == null
        || response.body().length() == null
        || response.body().length() == 0) {
      log.info(marker, "Response body is empty");
      log.info(marker, "Response status: " + response.status());
      return ResponseEntity.status(response.status()).build();
    }

    String bodyAsString =
        StreamUtils.copyToString(response.body().asInputStream(), StandardCharsets.UTF_8);
    if (bodyAsString.trim().isEmpty()) {
      log.info(marker, "Response body is blank");
      log.info(marker, "Response status: " + response.status());
      return ResponseEntity.status(response.status()).build();
    }

    log.info(marker, "Response status: " + response.status());
    Object decodedBody = springDecoder.decode(response, type);
    return ResponseEntity.status(response.status()).body(decodedBody);
  }
}
