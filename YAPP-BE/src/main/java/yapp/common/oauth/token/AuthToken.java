package yapp.common.oauth.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import java.security.Key;
import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AuthToken {

  @Getter
  private final String token;
  private final Key key;

  private static final String AUTHORITIES_KEY = "role";

  AuthToken(
    String id,
    Date expiry,
    Key key
  ) {
    this.key = key;
    this.token = createAuthToken(id, expiry);
  }

  AuthToken(
    String id,
    String role,
    Date expiry,
    Key key
  ) {
    this.key = key;
    this.token = createAuthToken(id, role, expiry);
  }

  private String createAuthToken(
    String id,
    Date expiry
  ) {
    return Jwts.builder()
      .setSubject(id)
      .signWith(key, SignatureAlgorithm.HS256)
      .setExpiration(expiry)
      .compact();
  }

  private String createAuthToken(
    String id,
    String role,
    Date expiry
  ) {
    return Jwts.builder()
      .setSubject(id)
      .claim(AUTHORITIES_KEY, role)
      .signWith(key, SignatureAlgorithm.HS256)
      .setExpiration(expiry)
      .compact();
  }

  public boolean validate() {
    return this.getTokenClaims() != null;
  }

  public Claims getTokenClaims() {
    try {
      return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
    } catch (SecurityException e) {
      log.info("JWT 서명 형식이 잘못되었습니다.");
    } catch (MalformedJwtException e) {
      log.info("유효하지 않는 JWT token 입니다.");
    } catch (ExpiredJwtException e) {
      log.info("만료된 JWT token 입니다.");
    } catch (UnsupportedJwtException e) {
      log.info("지원하지 않는 JWT token 입니다.");
    } catch (IllegalArgumentException e) {
      log.info("잘못된 JWT token 입니다.");
    }
    return null;
  }

  public Claims getExpiredTokenClaims() {
    try {
      Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
    } catch (ExpiredJwtException e) {
      log.info("Expired JWT token.");
      return e.getClaims();
    }
    return null;
  }
}
