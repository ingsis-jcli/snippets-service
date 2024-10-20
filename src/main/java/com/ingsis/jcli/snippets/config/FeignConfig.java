package com.ingsis.jcli.snippets.config;

import com.ingsis.jcli.snippets.clients.factory.FeignErrorDecoder;
import com.ingsis.jcli.snippets.common.Generated;
import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Generated
@Configuration
public class FeignConfig {

  @Bean
  Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }

  @Bean
  public ErrorDecoder feignErrorDecoder() {
    return new FeignErrorDecoder();
  }
}
