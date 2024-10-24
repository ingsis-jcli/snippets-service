package com.ingsis.jcli.snippets.common.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ingsis.jcli.snippets.common.Generated;
import java.util.List;

@Generated
public record DefaultRules(@JsonProperty("rules") List<DefaultRule> rules) {}
