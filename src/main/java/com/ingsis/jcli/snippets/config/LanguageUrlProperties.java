package com.ingsis.jcli.snippets.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "languages")
public class LanguageUrlProperties {
  private Map<String, String> urls;
}
