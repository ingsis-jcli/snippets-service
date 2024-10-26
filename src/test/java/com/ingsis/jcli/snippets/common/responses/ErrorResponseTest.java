package com.ingsis.jcli.snippets.common.responses;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ErrorResponseTest {

  @Test
  void testErrorResponseWithError() {
    String errorMessage = "This is an error message";
    ErrorResponse errorResponse = new ErrorResponse(errorMessage);
    assertThat(errorResponse.error()).isEqualTo(errorMessage);
    assertThat(errorResponse.hasError()).isTrue();
  }

  @Test
  void testErrorResponseWithoutError() {
    ErrorResponse errorResponse = new ErrorResponse();
    assertThat(errorResponse.error()).isNull();
    assertThat(errorResponse.hasError()).isFalse();
  }
}
