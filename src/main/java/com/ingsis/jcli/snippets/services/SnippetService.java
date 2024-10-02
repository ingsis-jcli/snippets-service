package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.dto.SnippetDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.repositories.SnippetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SnippetService {

  final SnippetRepository snippetRepository;
  final BlobStorageService blobStorageService;

  @Autowired
  public SnippetService(SnippetRepository snippetRepository, BlobStorageService blobStorageService) {
    this.snippetRepository = snippetRepository;
    this.blobStorageService = blobStorageService;
  }

  public Optional<Snippet> getSnippet(Long snippetId) {
    return snippetRepository.findSnippetById(snippetId);
  }

  public Snippet createSnippet(SnippetDto snippetDto) {
    String url = blobStorageService.uploadSnippet(snippetDto.getContent());

    Snippet snippet = new Snippet(
        snippetDto.getName(),
        url,
        snippetDto.getOwner()
    );
    return snippetRepository.save(snippet);
  }

  public boolean isOwner(Long snippetId, Long userId) {
    Optional<Snippet> snippet = getSnippet(snippetId);
    return snippet.filter(value -> userId.equals(value.getOwner())).isPresent();
  }

  public SnippetDto convertToDto(Snippet snippet) {
    String content = blobStorageService.downloadSnippet(snippet.getUrl());
    return new SnippetDto(
        snippet.getName(),
        content,
        snippet.getOwner()
    );
  }
}
