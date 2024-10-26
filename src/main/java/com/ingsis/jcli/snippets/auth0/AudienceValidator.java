package com.ingsis.jcli.snippets.auth0;

import com.ingsis.jcli.snippets.common.Generated;
import java.util.List;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

@Generated
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

  private final String audience;

  public AudienceValidator(String audience) {
    this.audience = audience;
  }

  @Override
  public OAuth2TokenValidatorResult validate(Jwt jwt) {
    OAuth2Error error = new OAuth2Error("invalid_token", "The required audience is missing", null);

    List<String> jwtAudience = jwt.getAudience();
    if (jwtAudience != null && jwtAudience.contains(audience)) {
      return OAuth2TokenValidatorResult.success();
    } else {
      return OAuth2TokenValidatorResult.failure(error);
    }
  }
}
