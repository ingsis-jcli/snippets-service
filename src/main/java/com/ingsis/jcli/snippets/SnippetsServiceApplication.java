package com.ingsis.jcli.snippets;

import com.ingsis.jcli.snippets.common.Generated;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Generated
@SpringBootApplication
@EnableFeignClients
public class SnippetsServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(SnippetsServiceApplication.class, args);
  }

  private static void loadEnv() {
    Dotenv dotenv = Dotenv.configure().directory("./").load();
    System.setProperty("POSTGRES_USER", dotenv.get("POSTGRES_USER"));
    System.setProperty("POSTGRES_PASSWORD", dotenv.get("POSTGRES_PASSWORD"));
    System.setProperty("POSTGRES_DB", dotenv.get("POSTGRES_DB"));
    System.setProperty("POSTGRES_PORT", dotenv.get("POSTGRES_PORT"));
    System.setProperty("PORT", dotenv.get("PORT"));
  }
}
