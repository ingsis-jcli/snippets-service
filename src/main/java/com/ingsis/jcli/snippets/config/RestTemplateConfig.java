package com.ingsis.jcli.snippets.config;

import com.ingsis.jcli.snippets.auth0.Auth0RestTemplateInterceptor;
import com.ingsis.jcli.snippets.common.Generated;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Generated
@Configuration
public class RestTemplateConfig {

  @Bean
  public RestTemplate restTemplate(Auth0RestTemplateInterceptor authInterceptor) {
    RestTemplate restTemplate = new RestTemplate();
    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
    interceptors.add(authInterceptor);
    restTemplate.setInterceptors(interceptors);

    return restTemplate;
  }
}
