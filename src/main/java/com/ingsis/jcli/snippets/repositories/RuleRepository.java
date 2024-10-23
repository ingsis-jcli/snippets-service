package com.ingsis.jcli.snippets.repositories;

import com.ingsis.jcli.snippets.models.Rule;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {
  Optional<Rule> findById(Long id);
}
