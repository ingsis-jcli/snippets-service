package com.ingsis.jcli.snippets.common.requests;

import com.ingsis.jcli.snippets.dto.SnippetDto;
import com.ingsis.jcli.snippets.models.Rule;
import java.util.List;

public record LintRequest(SnippetDto snippetDto, List<Rule> ruleList) {}
