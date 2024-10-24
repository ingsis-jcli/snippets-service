package com.ingsis.jcli.snippets.common.requests;

import com.ingsis.jcli.snippets.models.Rule;
import java.util.List;

public record AnalyzeRequest(String name, String url, List<Rule> rules, String version) {}
