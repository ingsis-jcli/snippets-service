package com.ingsis.jcli.snippets.repositories;

import com.ingsis.jcli.snippets.models.Snippet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SnippetRepository
    extends JpaRepository<Snippet, Long>, JpaSpecificationExecutor<Snippet> {
  Optional<Snippet> findSnippetById(Long id);

  List<Snippet> findAllByOwner(String userId);
}
