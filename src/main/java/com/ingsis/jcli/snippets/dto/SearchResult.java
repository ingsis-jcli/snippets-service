package com.ingsis.jcli.snippets.dto;

import com.ingsis.jcli.snippets.common.responses.SnippetResponse;
import java.util.List;

public record SearchResult(long count, List<SnippetResponse> snippets) {}
