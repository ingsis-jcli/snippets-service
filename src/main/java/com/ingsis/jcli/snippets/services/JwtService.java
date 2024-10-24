package com.ingsis.jcli.snippets.services;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final JwtDecoder jwtDecoder;

  public JwtService(JwtDecoder jwtDecoder) {
    this.jwtDecoder = jwtDecoder;
  }

  public Jwt extractJwt(String authHeader) {
    String jwt = authHeader.substring(7);
    return jwtDecoder.decode(jwt);
  }

  public String extractUserId(String authHeader) {
    Jwt jwt = extractJwt(authHeader);
    return jwt.getSubject();
  }
}
