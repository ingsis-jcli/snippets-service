package com.ingsis.jcli.snippets.repositories;

import com.ingsis.jcli.snippets.models.FormattingRules;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormattingRulesRepository extends JpaRepository<FormattingRules, Long> {
  Optional<FormattingRules> findAllByUserId(String userId);
}
