package com.ingsis.jcli.snippets.auth0;

import com.ingsis.jcli.snippets.common.Generated;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Generated
@Component
public class Auth0RestTemplateInterceptor implements ClientHttpRequestInterceptor {
  public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
  public static final String CORRELATION_ID_KEY = "correlationId";

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      HttpServletRequest httpServletRequest =
          ((ServletRequestAttributes) requestAttributes).getRequest();
      String authorizationHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
      if (authorizationHeader != null) {
        request.getHeaders().set(HttpHeaders.AUTHORIZATION, authorizationHeader);
      }
    }

    String correlationId = MDC.get(CORRELATION_ID_KEY);
    if (correlationId == null) {
      correlationId = UUID.randomUUID().toString();
      MDC.put(CORRELATION_ID_KEY, correlationId);
    }
    request.getHeaders().set(CORRELATION_ID_HEADER, correlationId);

    return execution.execute(request, body);
  }
}
