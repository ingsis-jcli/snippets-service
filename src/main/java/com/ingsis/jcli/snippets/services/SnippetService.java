package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.repositories.SnippetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SnippetService {

  final SnippetRepository snippetRepository;

  @Autowired
  public SnippetService(SnippetRepository snippetRepository) {
    this.snippetRepository = snippetRepository;
  }

  public Optional<Snippet> getSnippet(Long snippetId) {
    return snippetRepository.findSnippetById(snippetId);
  }
}
