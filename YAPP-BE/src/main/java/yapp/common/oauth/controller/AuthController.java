package yapp.common.oauth.controller;

import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yapp.common.config.AppProperties;
import yapp.common.oauth.entity.RoleType;
import yapp.common.oauth.token.AuthToken;
import yapp.common.oauth.token.AuthTokenProvider;
import yapp.common.response.ApiResponse;
import yapp.common.security.CurrentAuthPrincipal;
import yapp.common.utils.CookieUtil;
import yapp.common.utils.HeaderUtil;
import yapp.domain.member.entitiy.MemberRefreshToken;
import yapp.domain.member.repository.MemberRefreshTokenRepository;

@Slf4j
@Tag(name = "권한 인증")
@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AppProperties appProperties;
  private final AuthTokenProvider tokenProvider;
  private final MemberRefreshTokenRepository memberRefreshTokenRepository;
  private final static String REFRESH_TOKEN = "refresh_token";

  public AuthController(
    AppProperties appProperties,
    AuthTokenProvider tokenProvider,
    MemberRefreshTokenRepository memberRefreshTokenRepository
  ) {
    this.appProperties = appProperties;
    this.tokenProvider = tokenProvider;
    this.memberRefreshTokenRepository = memberRefreshTokenRepository;
  }

  @PostMapping("/login/confirm")
  @Operation(summary = "access_token으로 회원 ID , 권한 정보 확인")
  public ApiResponse checkLoginConfirm(
    @CurrentAuthPrincipal User memberPrincipal
  ) {
    Map<String, Object> result = new HashMap<>();
    result.put("회원 id", memberPrincipal.getUsername());
    result.put("회원 권한 정보", memberPrincipal.getAuthorities());

    return ApiResponse.success("소셜 로그인 회원 정보", result);
  }

  @PostMapping("/reissue/token")
  @Operation(summary = "access_token 재발급")
  public ApiResponse reissueRefreshToken(
    HttpServletRequest request
  ) {
    String accessToken = HeaderUtil.getAccessToken(request);
    String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
      .map(Cookie::getValue)
      .orElse((null));
    AuthToken authToken = tokenProvider.convertAuthToken(accessToken);

    Date now = new Date();
    Claims claims = authToken.getExpiredTokenClaims();
    if (claims == null) {
      return ApiResponse.notExpiredTokenYet();
    }
    String memberId = claims.getSubject();
    AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);
    RoleType roleType = RoleType.of(claims.get("role", String.class));

    if (!authToken.validate()) {
      log.info("만료된 Access 토큰입니다. 재발급이 필요합니다.");
      if (authRefreshToken.validate()) {
        log.info("유효한 Refresh 토큰입니다.");

        MemberRefreshToken memberRefreshToken = memberRefreshTokenRepository.findByMemberIdAndRefreshToken(
          memberId, refreshToken);
        if (memberRefreshToken == null) {
          log.info("Refresh 토큰이 null 입니다.");
          return ApiResponse.invalidRefreshToken();
        }
        if (memberRefreshToken.getRefreshToken().equals(refreshToken)) {
          log.info("access_token 재발급!");
          AuthToken newAccessToken = tokenProvider.createAuthToken(
            memberId,
            roleType.getCode(),
            new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
          );
          return ApiResponse.success("access_token", newAccessToken);
        }
        return ApiResponse.invalidRefreshToken();
      }
      return ApiResponse.invalidRefreshToken();
    }
    return ApiResponse.success("access_token", accessToken);
  }
}
