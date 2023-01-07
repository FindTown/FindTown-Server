package yapp.common.oauth.controller;

import io.jsonwebtoken.Claims;
import java.util.Date;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import yapp.common.config.AppProperties;
import yapp.common.oauth.entity.AuthReqModel;
import yapp.common.oauth.entity.RoleType;
import yapp.common.oauth.token.AuthToken;
import yapp.common.oauth.token.AuthTokenProvider;
import yapp.common.response.ApiResponse;
import yapp.common.utils.CookieUtil;
import yapp.common.utils.HeaderUtil;
import yapp.domain.member.entitiy.MemberPrincipal;
import yapp.domain.member.entitiy.MemberRefreshToken;
import yapp.domain.member.repository.MemberRefreshTokenRepository;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final AppProperties appProperties;
  private final AuthTokenProvider tokenProvider;
  private final AuthenticationManager authenticationManager;
  private final MemberRefreshTokenRepository memberRefreshTokenRepository;

  private final static long THREE_DAYS_MSEC = 259200000;
  private final static String REFRESH_TOKEN = "refresh_token";

  @PostMapping("/login")
  public ApiResponse login(
    HttpServletRequest request,
    HttpServletResponse response,
    @RequestBody AuthReqModel authReqModel
  ) {
    log.info("/login");
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        authReqModel.getId(),
        authReqModel.getPassword()
      )
    );

    String email = authReqModel.getId();
    SecurityContextHolder.getContext().setAuthentication(authentication);

    Date now = new Date();
    AuthToken accessToken = tokenProvider.createAuthToken(
      email,
      ((MemberPrincipal) authentication.getPrincipal()).getRoleType().getCode(),
      new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
    );

    long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
    AuthToken refreshToken = tokenProvider.createAuthToken(
      appProperties.getAuth().getTokenSecret(),
      new Date(now.getTime() + refreshTokenExpiry)
    );

    MemberRefreshToken memberRefreshToken = memberRefreshTokenRepository.findByMemberId(email);
    if (memberRefreshToken == null) {
      memberRefreshToken = new MemberRefreshToken(email, refreshToken.getToken());
      memberRefreshTokenRepository.saveAndFlush(memberRefreshToken);
    } else {
      memberRefreshToken.setRefreshToken(refreshToken.getToken());
    }

    int cookieMaxAge = (int) refreshTokenExpiry / 60;
    CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
    CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

    return ApiResponse.success("token", accessToken.getToken());
  }

  @GetMapping("/refresh")
  public ApiResponse refreshToken(
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    String accessToken = HeaderUtil.getAccessToken(request);
    AuthToken authToken = tokenProvider.convertAuthToken(accessToken);
    if (!authToken.validate()) {
      return ApiResponse.invalidAccessToken();
    }

    Claims claims = authToken.getExpiredTokenClaims();
    if (claims == null) {
      return ApiResponse.notExpiredTokenYet();
    }

    String memberId = claims.getSubject();
    RoleType roleType = RoleType.of(claims.get("role", String.class));

    String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
      .map(Cookie::getValue)
      .orElse((null));
    AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);

    if (authRefreshToken.validate()) {
      return ApiResponse.invalidRefreshToken();
    }

    MemberRefreshToken memberRefreshToken = memberRefreshTokenRepository.findByMemberIdAndRefreshToken(
      memberId, refreshToken);
    if (memberRefreshToken == null) {
      return ApiResponse.invalidRefreshToken();
    }

    Date now = new Date();
    AuthToken newAccessToken = tokenProvider.createAuthToken(
      memberId,
      roleType.getCode(),
      new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
    );

    long validTime = authRefreshToken.getTokenClaims().getExpiration().getTime() - now.getTime();

    if (validTime <= THREE_DAYS_MSEC) {
      long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

      authRefreshToken = tokenProvider.createAuthToken(
        appProperties.getAuth().getTokenSecret(),
        new Date(now.getTime() + refreshTokenExpiry)
      );

      memberRefreshToken.setRefreshToken(authRefreshToken.getToken());

      int cookieMaxAge = (int) refreshTokenExpiry / 60;
      CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
      CookieUtil.addCookie(response, REFRESH_TOKEN, authRefreshToken.getToken(), cookieMaxAge);
    }

    return ApiResponse.success("token", newAccessToken.getToken());
  }
}
