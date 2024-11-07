package com.ingsis.jcli.snippets.controllers;

import static com.ingsis.jcli.snippets.services.BlobStorageService.getBaseUrl;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingsis.jcli.snippets.common.language.LanguageSuccess;
import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.common.responses.SnippetResponse;
import com.ingsis.jcli.snippets.common.status.ProcessStatus;
import com.ingsis.jcli.snippets.dto.SnippetDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.repositories.SnippetRepository;
import com.ingsis.jcli.snippets.services.BlobStorageService;
import com.ingsis.jcli.snippets.services.JwtService;
import com.ingsis.jcli.snippets.services.LanguageService;
import com.ingsis.jcli.snippets.services.PermissionService;
import com.ingsis.jcli.snippets.services.SnippetService;
import com.ingsis.jcli.snippets.services.TestCaseService;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SnippetControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private SnippetService snippetService;
  @MockBean private PermissionService permissionService;
  @MockBean private LanguageService languageService;
  @MockBean private BlobStorageService blobStorageService;
  @MockBean private JwtDecoder jwtDecoder;
  @MockBean private JwtService jwtService;
  @MockBean private TestCaseService testCaseService;

  @Autowired private ObjectMapper objectMapper;

  private static final String path = "/snippet";
  private static final LanguageVersion languageVersion = new LanguageVersion("printscript", "1.1");
  @Autowired private SnippetRepository snippetRepository;
  @Autowired private SnippetController snippetController;

  private Jwt createMockJwt(String userId) {
    return Jwt.withTokenValue("mock-token")
        .header("alg", "none")
        .claim("user_id", userId)
        .claim("scope", "read")
        .build();
  }

  @Test
  void getSnippetOk() throws Exception {
    Long id = 1L;
    String userId = "123";
    String content = "This is the content of the snippet.";
    SnippetResponse expected =
        new SnippetResponse(
            1L, "name", content, "java", "21", "ps", ProcessStatus.NOT_STARTED, userId);

    Jwt mockJwt = createMockJwt(userId);

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    when(snippetService.canGetSnippet(id, userId)).thenReturn(true);
    when(snippetService.getSnippetDto(id)).thenReturn(expected);
    when(languageService.getExtension(new LanguageVersion("java", "21"))).thenReturn("java");

    mockMvc
        .perform(
            get(path)
                .param("snippetId", id.toString())
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("name"))
        .andExpect(jsonPath("$.content").value(content))
        .andExpect(jsonPath("$.language").value("java"))
        .andExpect(jsonPath("$.version").value("21"));
  }

  @Test
  void getSnippetNotFound() throws Exception {
    Long id = 1L;
    String userId = "123";

    Jwt mockJwt = createMockJwt(userId);

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(snippetService.canGetSnippet(id, userId)).thenThrow(NoSuchElementException.class);
    when(snippetService.getSnippetContent(id)).thenReturn(Optional.empty());
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            get(path)
                .param("snippetId", id.toString())
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isNotFound());
  }

  @Test
  void getSnippetForbidden() throws Exception {
    Long id = 1L;
    String userId = "123";

    Jwt mockJwt = createMockJwt(userId);

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(permissionService.hasPermissionOnSnippet(any(), anyLong())).thenReturn(false);
    when(snippetService.getSnippetContent(id)).thenReturn(Optional.of(""));
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            get(path)
                .param("snippetId", id.toString())
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isForbidden());
  }

  @Test
  void createSnippetSuccess() throws Exception {
    String userId = "123";
    SnippetDto snippetDto = new SnippetDto("name", "content", "printscript", "1.1");
    Snippet snippet = new Snippet("name", getBaseUrl(snippetDto, userId), userId, languageVersion);
    snippet.setId(1L);

    Jwt mockJwt = createMockJwt(userId);

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(snippetService.createSnippet(snippetDto, userId)).thenReturn(snippet);
    when(languageService.validateSnippet(snippet, languageVersion))
        .thenReturn(new LanguageSuccess());
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(snippetDto))
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$").value(1L));
  }

  @Test
  void createSnippetFailBlankDto() throws Exception {
    SnippetDto snippetDto = new SnippetDto("", "", "", "");

    Jwt mockJwt = createMockJwt("123");
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(snippetDto))
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void editSnippetSuccess() throws Exception {
    Long id = 1L;
    String userId = "123";
    String content = "new content";
    LanguageVersion languageVersion = new LanguageVersion("printscript", "1.1");
    Snippet snippet = new Snippet("name", "url", userId, languageVersion);
    snippet.setId(id);

    Jwt mockJwt = createMockJwt(userId);

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(snippetService.editSnippet(id, content, userId)).thenReturn(snippet);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .param("snippetId", id.toString())
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(id));
  }

  @Test
  void createSnippetFromUpload() throws Exception {
    String userId = "123";
    String content = "content";
    SnippetDto snippetDto = new SnippetDto("name", content, "printscript", "1.1");
    Snippet snippet = new Snippet("name", getBaseUrl(snippetDto, userId), userId, languageVersion);
    snippet.setId(1L);

    MockMultipartFile file =
        new MockMultipartFile(
            "file", // param name
            "snippet.txt",
            "text/plain",
            content.getBytes());

    Jwt mockJwt = createMockJwt(userId);

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(snippetService.createSnippet(snippetDto, userId)).thenReturn(snippet);
    when(languageService.validateSnippet(snippet, languageVersion))
        .thenReturn(new LanguageSuccess());
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            multipart(path + "/upload")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", snippetDto.getName())
                .param("language", snippetDto.getLanguage())
                .param("version", snippetDto.getVersion())
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$").value(1L));
  }

  @Test
  void editSnippetFromUpload() throws Exception {
    Long id = 1L;
    String userId = "123";
    String content = "content";
    SnippetDto snippetDto = new SnippetDto("name", content, "printscript", "1.1");
    Snippet snippet = new Snippet("name", getBaseUrl(snippetDto, userId), userId, languageVersion);
    snippet.setId(1L);

    MockMultipartFile file =
        new MockMultipartFile(
            "file", // param name
            "snippet.txt",
            "text/plain",
            content.getBytes());

    Jwt mockJwt = createMockJwt(userId);

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(snippetService.canEditSnippet(id, userId)).thenReturn(true);
    when(snippetService.editSnippet(id, snippetDto.getContent(), userId)).thenReturn(snippet);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            multipart(path + "/upload")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("snippetId", id.toString())
                .param("name", snippetDto.getName())
                .param("language", snippetDto.getLanguage())
                .param("version", snippetDto.getVersion())
                .header("Authorization", "Bearer mock-token")
                .with(
                    request -> {
                      request.setMethod("PUT");
                      return request;
                    })
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$").value(1L));
  }

  @Test
  void downloadSnippetSuccess() throws Exception {
    Long id = 1L;
    String userId = "123";
    String content = "This is the content of the snippet.";
    Snippet snippet = new Snippet("name", "url", userId, languageVersion);
    snippet.setId(id);
    snippet.setLanguageVersion(new LanguageVersion("printscript", "1.0"));

    Jwt mockJwt = createMockJwt(userId);

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(snippetService.getSnippet(id)).thenReturn(Optional.of(snippet));
    when(snippetService.canGetSnippet(id, userId)).thenReturn(true);
    when(snippetService.getSnippetContent(id)).thenReturn(Optional.of(content));
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);
    when(languageService.getExtension(new LanguageVersion("printscript", "1.0"))).thenReturn("ps");

    mockMvc
        .perform(
            get(path + "/download/{snippetId}", id)
                .param("formatted", "false")
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isOk())
        .andExpect(
            header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"name.ps\""))
        .andExpect(content().string(content));
  }

  @Test
  void downloadSnippetNotFound() throws Exception {
    Long id = 1L;
    String userId = "123";

    Jwt mockJwt = createMockJwt(userId);

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(snippetService.getSnippet(id)).thenReturn(Optional.empty());
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            get(path + "/download/{snippetId}", id)
                .param("formatted", "false")
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isNotFound());
  }

  @Test
  void downloadSnippetForbidden() throws Exception {
    Long id = 1L;
    String userId = "123";
    Snippet snippet = new Snippet("name", "url", userId, languageVersion);
    snippet.setId(id);

    Jwt mockJwt = createMockJwt(userId);

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(snippetService.getSnippet(id)).thenReturn(Optional.of(snippet));
    when(snippetService.canGetSnippet(id, userId)).thenReturn(false);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);
    when(languageService.getExtension(new LanguageVersion("printscript", "1.0"))).thenReturn("ps");

    mockMvc
        .perform(
            get(path + "/download/{snippetId}", id)
                .param("formatted", "false")
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getFileTypes() throws Exception {
    Map<LanguageVersion, String> map =
        Map.of(
            new LanguageVersion("printscript", "1.0"), "ps",
            new LanguageVersion("printscript", "1.1"), "ps");

    when(languageService.getAllExtensions()).thenReturn(map);

    Map<String, String> stringMap =
        map.entrySet().stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.getKey().getLanguage() + ":" + entry.getKey().getVersion(),
                    Map.Entry::getValue));

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(stringMap);

    String userId = "123";
    Jwt mockJwt = createMockJwt(userId);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

    mockMvc
        .perform(
            get(path + "/filetypes")
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isOk())
        .andExpect(content().json(json));
  }

  @Test
  void deleteSnippetSuccess() throws Exception {
    Long snippetId = 1L;
    String userId = "123";

    Jwt mockJwt = createMockJwt(userId);

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);
    doNothing().when(snippetService).deleteSnippet(snippetId, userId);

    mockMvc
        .perform(
            delete(path + "/" + snippetId)
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isOk());
  }

  @Test
  void deleteSnippetNotFound() throws Exception {
    Long snippetId = 1L;
    String userId = "123";

    Jwt mockJwt = createMockJwt(userId);

    when(jwtService.extractUserId(anyString())).thenReturn(userId);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);
    doThrow(new NoSuchElementException()).when(snippetService).deleteSnippet(snippetId, userId);

    mockMvc
        .perform(
            delete(path + "/" + snippetId)
                .header("Authorization", "Bearer mock-token")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(mockJwt)))
        .andExpect(status().isNotFound());
  }
}
