package com.ingsis.jcli.snippets.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:languages-test.properties")
@EnableConfigurationProperties(LanguageUrlProperties.class)
public class TestConfiguration {

  @Bean
  public LanguageUrlProperties languageUrlProperties() {
    return new LanguageUrlProperties();
  }
}
