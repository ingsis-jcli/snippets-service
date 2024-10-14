package com.ingsis.jcli.snippets.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "languages.url")
@Getter
public class LanguageUrlProperties {
  private String printscript;
}