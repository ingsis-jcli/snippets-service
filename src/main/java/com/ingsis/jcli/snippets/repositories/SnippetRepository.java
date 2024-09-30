package com.ingsis.jcli.snippets.repositories;

import com.ingsis.jcli.snippets.models.Snippet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SnippetRepository extends JpaRepository<Snippet, Long> {
  Optional<Snippet> findSnippetById(Long id);
}
