package com.ingsis.jcli.snippets.repositories;

import com.ingsis.jcli.snippets.models.Language;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {

  Optional<Language> findByName(String name);
}
