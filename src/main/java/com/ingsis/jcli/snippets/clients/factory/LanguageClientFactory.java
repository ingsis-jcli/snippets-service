package com.ingsis.jcli.snippets.clients.factory;

import com.ingsis.jcli.snippets.auth0.AuthFeignInterceptor;
import com.ingsis.jcli.snippets.clients.LanguageClient;
import com.ingsis.jcli.snippets.common.Generated;
import feign.Contract;
import feign.Feign;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.gson.GsonEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Generated
@Component
@Import(FeignClientsConfiguration.class)
public class LanguageClientFactory {

  private final RequestInterceptor authFeignInterceptor;
  private final Contract feignContract;
  private final ErrorDecoder feignErrorDecoder;

  @Autowired
  public LanguageClientFactory(
      AuthFeignInterceptor authFeignInterceptor,
      Contract feignContract,
      ErrorDecoder feignErrorDecoder) {
    this.authFeignInterceptor = authFeignInterceptor;
    this.feignContract = feignContract;
    this.feignErrorDecoder = feignErrorDecoder;
  }

  public LanguageClient createClient(String baseUrl) {
    LanguageClient target =
        Feign.builder()
            .encoder(new GsonEncoder())
            .decoder(new FeignDecoder())
            .contract(feignContract)
            .errorDecoder(feignErrorDecoder)
            .requestInterceptor(authFeignInterceptor)
            .target(LanguageClient.class, baseUrl);
    return target;
  }
}
