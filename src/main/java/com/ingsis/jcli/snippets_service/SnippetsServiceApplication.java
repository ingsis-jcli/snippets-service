package com.ingsis.jcli.snippets_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class SnippetsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnippetsServiceApplication.class, args);
	}

}
