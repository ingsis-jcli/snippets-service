package com.ingsis.jcli.snippets.repositories;

import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.models.TestCase;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
  Optional<TestCase> findTestCaseById(Long id);

  List<TestCase> findAllBySnippet(Snippet snippet);

  List<TestCase> findAllBySnippet_Owner(String owner);
}
