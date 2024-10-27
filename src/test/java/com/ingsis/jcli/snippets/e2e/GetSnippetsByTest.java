package com.ingsis.jcli.snippets.e2e;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.common.status.ProcessStatus;
import com.ingsis.jcli.snippets.controllers.RuleController;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.repositories.SnippetRepository;
import com.ingsis.jcli.snippets.services.BlobStorageService;
import com.ingsis.jcli.snippets.services.JwtService;
import com.ingsis.jcli.snippets.services.PermissionService;
import com.ingsis.jcli.snippets.services.SnippetService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
public class GetSnippetsByTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private SnippetService snippetService;
  @Autowired private SnippetRepository snippetRepository;

  @MockBean private PermissionService permissionService;
  @MockBean private BlobStorageService blobStorageService;
  @MockBean private RuleController ruleController;
  @MockBean private JwtDecoder jwtDecoder;
  @MockBean private JwtService jwtService;

  private static final String token = "Bearer token";
  private final int total = 14;

  private Jwt createMockJwt(String userId) {
    return Jwt.withTokenValue("mock-token")
        .header("alg", "none")
        .claim("user_id", userId)
        .claim("scope", "write")
        .build();
  }

  @BeforeEach
  public void setup() {
    List<Snippet> snippets =
        List.of(
            new Snippet(
                "DataProcessor",
                "http://example.com/1",
                "user1",
                new LanguageVersion("printscript", "1.0")),
            new Snippet(
                "WebScraper",
                "http://example.com/2",
                "user2",
                new LanguageVersion("printscript", "1.0")),
            new Snippet(
                "InventoryManager",
                "http://example.com/3",
                "user3",
                new LanguageVersion("printscript", "1.1")),
            new Snippet(
                "ChatServer",
                "http://example.com/4",
                "user4",
                new LanguageVersion("printscript", "1.1")),
            new Snippet(
                "FinanceTracker",
                "http://example.com/5",
                "user1",
                new LanguageVersion("lua", "5.1")),
            new Snippet(
                "MachineLearningModel",
                "http://example.com/6",
                "user5",
                new LanguageVersion("Java", "15")),
            new Snippet(
                "DatabaseConnector",
                "http://example.com/7",
                "user6",
                new LanguageVersion("Java", "17")),
            new Snippet(
                "TaskScheduler",
                "http://example.com/8",
                "user3",
                new LanguageVersion("Java", "17")),
            new Snippet(
                "WeatherApp", "http://example.com/9", "user2", new LanguageVersion("Java", "17")),
            new Snippet(
                "WeatherApp2", "http://example.com/10", "user2", new LanguageVersion("Java", "21")),
            new Snippet(
                "WeatherApp3", "http://example.com/11", "user2", new LanguageVersion("Java", "21")),
            new Snippet(
                "ECommerceAPI",
                "http://example.com/12",
                "user4",
                new LanguageVersion("Go", "1.15")),
            new Snippet(
                "NotificationService",
                "http://example.com/13",
                "user1",
                new LanguageVersion("lua", "5.1")),
            new Snippet(
                "AnalyticsTool",
                "http://example.com/14",
                "user7",
                new LanguageVersion("R", "4.0")));
    for (int i = 0; i < 5; i++) {
      snippets.get(i).getStatus().setLinting(ProcessStatus.COMPLIANT);
    }
    for (int i = 5; i < snippets.size(); i++) {
      snippets.get(i).getStatus().setLinting(ProcessStatus.PENDING);
    }
    snippetRepository.saveAll(snippets);
    when(blobStorageService.getSnippet(anyString(), anyString()))
        .thenReturn(Optional.of("content"));
  }

  public void setupJwt(String userId) {
    Jwt mockJwt = createMockJwt(userId);
    when(jwtService.extractUserId(token)).thenReturn(userId);
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);
  }

  @Test
  @Transactional
  public void getAll() throws Exception {
    String userShared = "shared";
    setupJwt(userShared);

    List<Long> snippetIds = snippetRepository.findAll().stream().map(Snippet::getId).toList();
    when(permissionService.getSnippetsSharedWithUser(userShared)).thenReturn(snippetIds);

    mockMvc
        .perform(
            get("/snippet/search")
                .param("size", "100")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(total));
  }

  @Test
  @Transactional
  public void getFirstPage() throws Exception {
    String userShared = "shared";
    setupJwt(userShared);

    List<Long> snippetIds = snippetRepository.findAll().stream().map(Snippet::getId).toList();
    when(permissionService.getSnippetsSharedWithUser(userShared)).thenReturn(snippetIds);

    mockMvc
        .perform(
            get("/snippet/search")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(10));
  }

  @Test
  @Transactional
  public void getOwnedByUser1() throws Exception {
    String userId = "user1";
    setupJwt(userId);

    mockMvc
        .perform(
            get("/snippet/search")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3));
  }

  @Test
  @Transactional
  public void getAllForUser2() throws Exception {
    String userId = "user2";
    setupJwt(userId);

    // all of user1's (3) are shared with user2
    List<Long> snippetIds =
        snippetRepository.findAllByOwner("user1").stream().map(Snippet::getId).toList();
    when(permissionService.getSnippetsSharedWithUser(userId)).thenReturn(snippetIds);

    mockMvc
        .perform(
            get("/snippet/search")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(7));
  }

  @Test
  @Transactional
  public void getOwnedByUser2() throws Exception {
    String userId = "user2";
    setupJwt(userId);

    // all of user1's (3) are shared with user2
    List<Long> snippetIds =
        snippetRepository.findAllByOwner("user1").stream().map(Snippet::getId).toList();
    when(permissionService.getSnippetsSharedWithUser(userId)).thenReturn(snippetIds);

    mockMvc
        .perform(
            get("/snippet/search")
                .param("shared", "false")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(4));
  }

  @Test
  @Transactional
  public void getSharedWithUser2() throws Exception {
    String userId = "user2";
    setupJwt(userId);

    // all of user1's (3) are shared with user2
    List<Long> snippetIds =
        snippetRepository.findAllByOwner("user1").stream().map(Snippet::getId).toList();
    when(permissionService.getSnippetsSharedWithUser(userId)).thenReturn(snippetIds);

    mockMvc
        .perform(
            get("/snippet/search")
                .param("owner", "false")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3));
  }

  @Test
  @Transactional
  public void getOwnedByUser2WithPrintscript() throws Exception {
    String userId = "user2";
    setupJwt(userId);

    mockMvc
        .perform(
            get("/snippet/search")
                .param("language", "printscript")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1));

    mockMvc
        .perform(
            get("/snippet/search")
                .param("language", "PRINTSCRIPT")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  @Transactional
  public void getOwnedByUser2WithJava() throws Exception {
    String userId = "user2";
    setupJwt(userId);

    mockMvc
        .perform(
            get("/snippet/search")
                .param("language", "java")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3));
  }

  @Test
  @Transactional
  public void getOwnedWithUser1WithJava() throws Exception {
    String userId = "user1";
    setupJwt(userId);

    // all of user2's (4) are shared with user1
    List<Long> snippetIds =
        snippetRepository.findAllByOwner("user2").stream().map(Snippet::getId).toList();
    when(permissionService.getSnippetsSharedWithUser(userId)).thenReturn(snippetIds);

    mockMvc
        .perform(
            get("/snippet/search")
                .param("shared", "false")
                .param("language", "java")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @Transactional
  public void getSharedWithUser1WithJava() throws Exception {
    String userId = "user1";
    setupJwt(userId);

    // all of user2's (4) are shared with user1
    List<Long> snippetIds =
        snippetRepository.findAllByOwner("user2").stream().map(Snippet::getId).toList();
    when(permissionService.getSnippetsSharedWithUser(userId)).thenReturn(snippetIds);

    mockMvc
        .perform(
            get("/snippet/search")
                .param("language", "java")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3));
  }

  @Test
  @Transactional
  public void getOwnedByUser2StartingWith() throws Exception {
    String userId = "user2";
    setupJwt(userId);

    mockMvc
        .perform(
            get("/snippet/search")
                .param("name", "we")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(4));
  }

  @Test
  @Transactional
  public void getCompliantByUser1() throws Exception {
    String userId = "user1";
    setupJwt(userId);

    mockMvc
        .perform(
            get("/snippet/search")
                .param("lintingStatus", "COMPLIANT")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  @Transactional
  public void getCompliantByUser1WithLua() throws Exception {
    String userId = "user1";
    setupJwt(userId);

    mockMvc
        .perform(
            get("/snippet/search")
                .param("lintingStatus", "COMPLIANT")
                .param("language", "LUA")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  @Transactional
  public void getOwnedByUser2StartingWith2() throws Exception {
    String userId = "user2";
    setupJwt(userId);

    mockMvc
        .perform(
            get("/snippet/search")
                .param("name", "weather")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3));
  }

  @Test
  @Transactional
  public void getPendingByUser2() throws Exception {
    String userId = "user2";
    setupJwt(userId);

    mockMvc
        .perform(
            get("/snippet/search")
                .param("lintingStatus", "PENDING")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3));
  }

  @Test
  @Transactional
  public void getSpecificUser1() throws Exception {
    String userId = "user1";
    setupJwt(userId);

    mockMvc
        .perform(
            get("/snippet/search")
                .param("owner", "true")
                .param("shared", "false")
                .param("language", "lua")
                .param("name", "notif")
                .param("lintingStatus", "PENDING")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  @Transactional
  public void notSupportedLanguage() throws Exception {
    String userId = "user1";
    setupJwt(userId);

    mockMvc
        .perform(
            get("/snippet/search")
                .param("language", "kotlin")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }
}