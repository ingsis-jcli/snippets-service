package com.ingsis.jcli.snippets.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "languages.url")
public class LanguageUrlProperties {
  private String printscript;
}
