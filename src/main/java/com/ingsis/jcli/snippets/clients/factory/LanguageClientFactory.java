package com.ingsis.jcli.snippets.clients.factory;

import com.ingsis.jcli.snippets.auth0.AuthFeignInterceptor;
import com.ingsis.jcli.snippets.clients.LanguageClient;
import com.ingsis.jcli.snippets.common.Generated;
import feign.Feign;
import feign.RequestInterceptor;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated
@Component
public class LanguageClientFactory {
  
  private final RequestInterceptor authFeignInterceptor;
  
  @Autowired
  public LanguageClientFactory(AuthFeignInterceptor authFeignInterceptor) {
    this.authFeignInterceptor = authFeignInterceptor;
  }

  public LanguageClient createClient(String baseUrl) {
    LanguageClient target =
        Feign.builder()
            .encoder(new GsonEncoder())
            .decoder(new GsonDecoder())
            .requestInterceptor(authFeignInterceptor)
            .target(LanguageClient.class, baseUrl);
    return target;
  }
}
