package com.ingsis.jcli.snippets.config;

import com.ingsis.jcli.snippets.auth0.AuthFeignInterceptor;
import com.ingsis.jcli.snippets.clients.factory.FeignErrorDecoder;
import com.ingsis.jcli.snippets.clients.factory.LanguageClientFactory;
import com.ingsis.jcli.snippets.common.Generated;
import feign.Contract;
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

  @Bean
  public LanguageClientFactory languageClientFactory(
      AuthFeignInterceptor authFeignInterceptor,
      Contract feignContract,
      ErrorDecoder feignErrorDecoder) {
    return new LanguageClientFactory(authFeignInterceptor, feignContract, feignErrorDecoder);
  }
}
