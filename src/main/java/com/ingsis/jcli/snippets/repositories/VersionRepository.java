package com.ingsis.jcli.snippets.repositories;

import com.ingsis.jcli.snippets.models.Language;
import com.ingsis.jcli.snippets.models.Version;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {

  Optional<Version> findByVersionAndLanguage(String version, Language language);
}
