package com.ingsis.jcli.snippets;

import com.ingsis.jcli.snippets.config.LanguageProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:languages-test.properties")
@EnableConfigurationProperties(LanguageProperties.class)
public class TestConfiguration {

  @Bean
  public LanguageProperties languageUrlProperties() {
    return new LanguageProperties();
  }
}
