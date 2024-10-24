package com.ingsis.jcli.snippets.common.requests;

import java.util.List;

public record FormatRequest(String name, String url, List<RuleDto> rules, String version) {}