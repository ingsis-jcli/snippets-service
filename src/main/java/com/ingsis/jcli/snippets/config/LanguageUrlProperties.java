package com.ingsis.jcli.snippets.config;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "languages")
public class LanguageUrlProperties {
  private Map<String, String> urls;
}
