package com.ingsis.jcli.snippets.auth0;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class AuthFeignInterceptor implements RequestInterceptor {

  @Override
  public void apply(RequestTemplate template) {
    log.info("Intercepting request: " + template.url());
    final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      final HttpServletRequest httpServletRequest =
          ((ServletRequestAttributes) requestAttributes).getRequest();
      template.header(
          HttpHeaders.AUTHORIZATION, httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION));
      log.info("Header added: " + httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION));
    }
    log.info("Intercepted request: " + template.url());
  }
}
