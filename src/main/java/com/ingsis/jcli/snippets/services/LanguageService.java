package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.common.LanguageVersion;
import com.ingsis.jcli.snippets.models.Language;
import com.ingsis.jcli.snippets.models.Version;
import com.ingsis.jcli.snippets.repositories.LanguageRepository;
import com.ingsis.jcli.snippets.repositories.VersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class LanguageService {

  private final LanguageRepository languageRepository;
  private final VersionRepository versionRepository;

  @Autowired
  public LanguageService(LanguageRepository languageRepository, VersionRepository versionRepository) {
    this.languageRepository = languageRepository;
    this.versionRepository = versionRepository;
  }

  public LanguageVersion getLanguageVersion(String languageName, String versionName) {
    Optional<Language> language = languageRepository.findByName(languageName);
    if (language.isEmpty()) {
      throw new NoSuchElementException("No language found with name " + languageName);
    }

    Optional<Version> version = versionRepository
        .findByVersionAndLanguage(versionName, language.get());
    if (version.isEmpty()) {
      throw new NoSuchElementException("No version found with name " + versionName);
    }

    return new LanguageVersion(language.get(), version.get());
  }
}
