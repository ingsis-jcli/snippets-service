package com.ingsis.jcli.snippets.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.models.Rule;
import com.ingsis.jcli.snippets.services.JwtService;
import com.ingsis.jcli.snippets.services.LanguageService;
import com.ingsis.jcli.snippets.services.RulesService;
import com.ingsis.jcli.snippets.services.SnippetService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RuleControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private RulesService rulesService;
  @MockBean private LanguageService languageService;
  @MockBean private SnippetService snippetService;
  @MockBean private JwtDecoder jwtDecoder;
  @MockBean private JwtService jwtService;

  @Autowired private ObjectMapper objectMapper;

  private Jwt createMockJwt(String userId) {
    return Jwt.withTokenValue("mock-token")
        .header("alg", "none")
        .claim("user_id", userId)
        .claim("scope", "read")
        .build();
  }

  @Test
  void updateFormattingRulesSuccess() throws Exception {
    String userId = "123";
    Jwt mockJwt = createMockJwt(userId);
    LanguageVersion languageVersion = new LanguageVersion("printscript", "1.1");
    List<Rule> rules =
        List.of(new Rule("rule1", "value1", true), new Rule("rule2", "value2", false));

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(languageService.getLanguageVersion(anyString(), anyString())).thenReturn(languageVersion);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            put("/rules/formatting")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rules))
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isOk());
  }

  @Test
  void getFormattingRulesSuccess() throws Exception {
    String userId = "123";
    Jwt mockJwt = createMockJwt(userId);
    LanguageVersion languageVersion = new LanguageVersion("printscript", "1.1");
    List<Rule> rules =
        List.of(new Rule("rule1", "value1", true), new Rule("rule2", "value2", false));

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(languageService.getLanguageVersion(anyString(), anyString())).thenReturn(languageVersion);
    when(rulesService.getFormattingRules(anyString(), any(LanguageVersion.class)))
        .thenReturn(rules);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            get("/rules/formatting")
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("rule1")) // Corrected the value based on mock data
        .andExpect(jsonPath("$[0].value").value("value1")) // Added validation for value
        .andExpect(jsonPath("$[0].isActive").value(true)); // Added validation for isActive
  }

  @Test
  void getLintingRulesSuccess() throws Exception {
    String userId = "123";
    Jwt mockJwt = createMockJwt(userId);
    LanguageVersion languageVersion = new LanguageVersion("printscript", "1.1");
    List<Rule> rules =
        List.of(new Rule("rule1", "value1", true), new Rule("rule2", "value2", false));

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(languageService.getLanguageVersion(anyString(), anyString())).thenReturn(languageVersion);
    when(rulesService.getLintingRules(anyString(), any(LanguageVersion.class))).thenReturn(rules);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            get("/rules/linting")
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("rule1")) // Corrected the value based on mock data
        .andExpect(jsonPath("$[0].value").value("value1")) // Added validation for value
        .andExpect(jsonPath("$[0].isActive").value(true)); // Added validation for isActive
  }

  @Test
  void updateLintingRulesSuccess() throws Exception {
    String userId = "123";
    Jwt mockJwt = createMockJwt(userId);
    LanguageVersion languageVersion = new LanguageVersion("printscript", "1.1");
    List<Rule> rules =
        List.of(new Rule("rule1", "value1", true), new Rule("rule2", "value2", false));

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(languageService.getLanguageVersion(anyString(), anyString())).thenReturn(languageVersion);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            put("/rules/linting")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rules))
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isOk());
  }
}
