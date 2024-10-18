package com.ingsis.jcli.snippets.auth0;

import static org.junit.jupiter.api.Assertions.assertEquals;

import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class AuthFeignTests {

  private AuthFeignInterceptor authFeignInterceptor;

  @BeforeEach
  void setUp() {
    authFeignInterceptor = new AuthFeignInterceptor();
  }

  @Test
  void apply_shouldAddAuthorizationHeader_whenRequestAttributesPresent() {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Mockito.when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer test-token");
    RequestAttributes requestAttributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(requestAttributes);
    RequestTemplate requestTemplate = new RequestTemplate();
    authFeignInterceptor.apply(requestTemplate);
    assertEquals(
        "Bearer test-token",
        requestTemplate.headers().get(HttpHeaders.AUTHORIZATION).iterator().next());
  }
}
