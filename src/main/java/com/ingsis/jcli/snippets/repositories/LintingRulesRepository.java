package com.ingsis.jcli.snippets.repositories;

import com.ingsis.jcli.snippets.models.LintingRules;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LintingRulesRepository extends JpaRepository<LintingRules, Long> {
  Optional<LintingRules> findByUserId(String userId);
}
