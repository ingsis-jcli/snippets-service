package com.ingsis.jcli.snippets;

import com.ingsis.jcli.snippets.common.Generated;
import com.ingsis.jcli.snippets.config.LanguageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Generated
@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(LanguageProperties.class)
public class SnippetsServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(SnippetsServiceApplication.class, args);
  }
}
