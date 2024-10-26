// package com.ingsis.jcli.snippets.advice;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.ingsis.jcli.snippets.common.exceptions.ErrorFetchingClientData;
// import com.ingsis.jcli.snippets.common.exceptions.InvalidSnippetException;
// import com.ingsis.jcli.snippets.common.exceptions.NoSuchLanguageException;
// import com.ingsis.jcli.snippets.common.language.LanguageVersion;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.security.oauth2.jwt.Jwt;
// import org.springframework.security.oauth2.jwt.JwtDecoder;
// import
// org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
// @WebMvcTest({GlobalExceptionHandler.class, GlobalExceptionHandlerTest.ExceptionController.class})
// @ActiveProfiles("test")
// class GlobalExceptionHandlerTest {
//
//  @Autowired private MockMvc mockMvc;
//
//  @MockBean private JwtDecoder jwtDecoder;
//
//  @Autowired private ObjectMapper objectMapper;
//
//  private Jwt createMockJwt(String userId) {
//    return Jwt.withTokenValue("mock-token")
//      .header("alg", "none")
//      .claim("user_id", userId)
//      .claim("scope", "read")
//      .build();
//  }
//
//  private static final LanguageVersion languageVersion = new LanguageVersion("printscript",
// "1.1");
//
//  @RestController
//  @RequestMapping("/exception")
//  static class ExceptionController {
//    @GetMapping("/invalid-snippet")
//    public void throwInvalidSnippetException() {
//      throw new InvalidSnippetException("name", languageVersion);
//    }
//
//    @GetMapping("/no-such-language")
//    public void throwNoSuchLanguageException() {
//      throw new NoSuchLanguageException(languageVersion.getLanguage());
//    }
//
//    @GetMapping("/error-fetching-client-data")
//    public void throwErrorFetchingClientData() {
//      throw new ErrorFetchingClientData("Failed to fetch client data", languageVersion);
//    }
//  }
//
//  @BeforeEach
//  void setUp() {
//    Mockito.reset(jwtDecoder);
//  }
//
//  @Test
//  void handleInvalidSnippetException() throws Exception {
//    String userId = "123";
//    Jwt mockJwt = createMockJwt(userId);
//    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);
//    mockMvc
//      .perform(
//        get("/exception/invalid-snippet")
//          .contentType(MediaType.APPLICATION_JSON)
//          .header("Authorization", "Bearer mock-token")
//          .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
//      .andExpect(status().isBadRequest())
//      .andExpect(content().string("Error validating snippet: name"));
//  }
//
//  @Test
//  void handleNoSuchLanguageException() throws Exception {
//    mockMvc
//      .perform(
//        get("/exception/no-such-language")
//          .contentType(MediaType.APPLICATION_JSON)
//          .with(SecurityMockMvcRequestPostProcessors.jwt()))
//      .andExpect(status().isBadRequest())
//      .andExpect(content().string("No such language: " + languageVersion.getLanguage()));
//  }
//
//  @Test
//  void handleErrorFetchingClientData() throws Exception {
//    mockMvc
//      .perform(
//        get("/exception/error-fetching-client-data")
//          .contentType(MediaType.APPLICATION_JSON)
//          .with(SecurityMockMvcRequestPostProcessors.jwt()))
//      .andExpect(status().isInternalServerError())
//      .andExpect(content().string("Error getting data from the client " +
// languageVersion.toString() + " : " + "Failed to fetch client data"));
//  }
// }
