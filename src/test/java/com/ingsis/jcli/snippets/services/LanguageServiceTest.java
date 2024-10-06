package com.ingsis.jcli.snippets.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.ingsis.jcli.snippets.common.LanguageVersion;
import com.ingsis.jcli.snippets.models.Language;
import com.ingsis.jcli.snippets.models.Version;
import com.ingsis.jcli.snippets.repositories.LanguageRepository;
import com.ingsis.jcli.snippets.repositories.VersionRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class LanguageServiceTest {

  @MockBean private LanguageRepository languageRepository;

  @MockBean private VersionRepository versionRepository;

  @Autowired private LanguageService languageService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testSuccess() {
    String languageName = "PrintScript";
    String versionName = "1.0";
    Language language = new Language();
    language.setName(languageName);
    Version version = new Version();
    version.setVersion(versionName);
    version.setLanguage(language);
    language.setVersions(Set.of(version));
    when(languageRepository.findByName(languageName)).thenReturn(Optional.of(language));
    when(versionRepository.findByVersionAndLanguage(versionName, language))
        .thenReturn(Optional.of(version));
    LanguageVersion result = languageService.getLanguageVersion(languageName, versionName);
    assertThat(result).isNotNull();
    assertThat(result.getLanguage()).isEqualTo(language);
    assertThat(result.getVersion()).isEqualTo(version);
  }

  @Test
  void testLanguageNotFound() {
    String languageName = "UnknownLanguage";
    String versionName = "1.0";
    when(languageRepository.findByName(languageName)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> languageService.getLanguageVersion(languageName, versionName))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("No language found with name " + languageName);
  }

  @Test
  void testVersionNotFound() {
    String languageName = "PrintScript";
    String versionName = "UnknownVersion";
    Language language = new Language();
    language.setName(languageName);
    when(languageRepository.findByName(languageName)).thenReturn(Optional.of(language));
    when(versionRepository.findByVersionAndLanguage(versionName, language))
        .thenReturn(Optional.empty());
    assertThatThrownBy(() -> languageService.getLanguageVersion(languageName, versionName))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("No version found with name " + versionName);
  }
}
