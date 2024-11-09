package com.ingsis.jcli.snippets.server;

import static com.ingsis.jcli.snippets.server.CorrelationIdFilter.CORRELATION_ID_HEADER;
import static com.ingsis.jcli.snippets.server.CorrelationIdFilter.CORRELATION_ID_KEY;

import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class CorrelationIdInterceptor implements ClientHttpRequestInterceptor {
  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    String correlationId = MDC.get(CORRELATION_ID_KEY);
    if (correlationId != null) {
      request.getHeaders().set(CORRELATION_ID_HEADER, correlationId);
    }
    return execution.execute(request, body);
  }
}
