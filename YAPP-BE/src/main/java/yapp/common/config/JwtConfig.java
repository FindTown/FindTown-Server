package yapp.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import yapp.common.oauth.token.AuthTokenProvider;

@Configuration
public class JwtConfig {
  @Value("${jwt.secret}")
  private String secret;

  @Bean
  public AuthTokenProvider jwtProvider() {
    return new AuthTokenProvider(secret);
  }
}
