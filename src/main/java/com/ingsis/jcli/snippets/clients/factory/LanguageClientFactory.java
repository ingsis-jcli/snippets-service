package com.ingsis.jcli.snippets.clients.factory;

import com.ingsis.jcli.snippets.clients.LanguageClient;
import com.ingsis.jcli.snippets.common.Generated;
import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.springframework.stereotype.Component;

@Generated
@Component
public class LanguageClientFactory {

  public LanguageClient createClient(String baseUrl) {
    LanguageClient target =
        Feign.builder()
            .encoder(new GsonEncoder())
            .decoder(new GsonDecoder())
            .target(LanguageClient.class, baseUrl);
    return target;
  }
}
