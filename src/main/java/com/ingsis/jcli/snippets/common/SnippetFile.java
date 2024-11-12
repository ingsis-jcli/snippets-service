package com.ingsis.jcli.snippets.common;

import org.springframework.core.io.Resource;

public record SnippetFile(Resource file, String filename, String extension) {}
