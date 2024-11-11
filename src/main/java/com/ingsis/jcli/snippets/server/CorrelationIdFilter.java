package com.ingsis.jcli.snippets.server;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class CorrelationIdFilter implements Filter {

  public static final String CORRELATION_ID_KEY = "correlation-id";
  public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      HttpServletResponse httpResponse = (HttpServletResponse) response;

      String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
      if (correlationId == null) {
        correlationId = UUID.randomUUID().toString();
      }

      MDC.put(CORRELATION_ID_KEY, correlationId);

      httpResponse.setHeader(CORRELATION_ID_KEY, correlationId);

      try {
        chain.doFilter(request, response);
      } finally {
        MDC.remove(CORRELATION_ID_KEY);
      }
    } else {
      chain.doFilter(request, response);
    }
  }
}
