package yapp.common.oauth.controller;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN;
import static yapp.common.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository.REFRESH_TOKEN;

import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yapp.common.config.AppProperties;
import yapp.common.config.Const;
import yapp.common.oauth.entity.RoleType;
import yapp.common.oauth.service.AuthService;
import yapp.common.oauth.token.AuthToken;
import yapp.common.oauth.token.AuthTokenProvider;
import yapp.common.response.ApiResponse;
import yapp.common.response.ApiResponseHeader;
import yapp.common.security.CurrentAuthPrincipal;
import yapp.common.utils.CookieUtil;
import yapp.common.utils.HeaderUtil;
import yapp.domain.member.dto.request.MemberSignInRequest;
import yapp.domain.member.entitiy.MemberRefreshToken;
import yapp.domain.member.repository.MemberRefreshTokenRepository;

@Slf4j
@Tag(name = "권한 인증")
@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;
  private final AppProperties appProperties;
  private final AuthTokenProvider authTokenProvider;
  private final RedisTemplate redisTemplate;
  private final MemberRefreshTokenRepository memberRefreshTokenRepository;

  public AuthController(
    AuthService authService,
    AppProperties appProperties,
    AuthTokenProvider authTokenProvider,
    RedisTemplate redisTemplate,
    MemberRefreshTokenRepository memberRefreshTokenRepository
  ) {
    this.authService = authService;
    this.appProperties = appProperties;
    this.authTokenProvider = authTokenProvider;
    this.redisTemplate = redisTemplate;
    this.memberRefreshTokenRepository = memberRefreshTokenRepository;
  }

  @PostMapping("/login")
  @Operation(summary = "소셜 로그인(카카오, 애플)")
  @Tag(name = "[화면]-로그인/회원가입")
  public ApiResponse login(
    HttpServletRequest request,
    HttpServletResponse response,
    @RequestBody MemberSignInRequest memberSignInRequest
  ) {

    Map<String, String> result = authService.login(memberSignInRequest);
    int registerCheck = Integer.parseInt(result.get("register_check"));

    switch (registerCheck) {
      case Const.USE_MEMBERS:
        long refreshTokenExpiry = Long.parseLong(result.get("refresh_token_expiry"));
        int cookieMaxAge = (int) refreshTokenExpiry / 60;
        int cookieMaxAgeForAccess = (int) appProperties.getAuth().getTokenExpiry() / 1000;

        // access_token 담기
        CookieUtil.deleteCookie(request, response, ACCESS_TOKEN);
        CookieUtil.addCookieForAccess(
          response, ACCESS_TOKEN, result.get("access_token"), cookieMaxAgeForAccess);

        // refresh_token 담기
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.addCookie(response, REFRESH_TOKEN, result.get("refresh_token"), cookieMaxAge);

        return new ApiResponse(new ApiResponseHeader(200, "회원 계정입니다."), result);
      case Const.NON_MEMBERS:
        return new ApiResponse(new ApiResponseHeader(400, "비회원 계정입니다."), result);
    }
    return ApiResponse.fail();
  }

  @PostMapping("/login/confirm")
  @Operation(summary = "access_token으로 회원 ID , 권한 정보 확인")
  public ApiResponse checkLoginConfirm(
    @CurrentAuthPrincipal User memberPrincipal
  ) {
    Map<String, Object> result = new HashMap<>();
    result.put("회원 id", memberPrincipal.getUsername());
    result.put("회원 권한 정보", memberPrincipal.getAuthorities());

    return ApiResponse.success("", result);
  }

  @PostMapping("/reissue/token")
  @Operation(summary = "access_token 재발급")
  public ApiResponse reissueRefreshToken(
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    String accessToken = HeaderUtil.getAccessToken(request);
    String isUnable = (String) redisTemplate.opsForValue().get(accessToken);
    if (StringUtils.hasText(isUnable)) {
      return new ApiResponse(new ApiResponseHeader(401, "사용 불가능한 토큰입니다"), null);
    }

    String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
      .map(Cookie::getValue)
      .orElse((null));
    AuthToken authToken = authTokenProvider.convertAuthToken(accessToken);

    Date now = new Date();
    Claims claims = authToken.getTokenClaims();
    String memberId = claims.getSubject();
    AuthToken authRefreshToken = authTokenProvider.convertAuthToken(refreshToken);

    if (authRefreshToken.validate()) {

      Optional<MemberRefreshToken> memberRefreshToken = memberRefreshTokenRepository.findByMemberIdAndRefreshToken(
        memberId, refreshToken);
      if (memberRefreshToken.isEmpty()) {
        log.info("Refresh 토큰이 null 입니다.");
        return ApiResponse.invalidRefreshToken();
      }
      if (memberRefreshToken.get().getRefreshToken().equals(refreshToken)) {
        //기존 토큰 사용 제한
        Long expiration = authTokenProvider.getExpiration(accessToken);
        redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

        //새 토큰 발급
        AuthToken newAccessToken = authTokenProvider.createAuthToken(
          memberId,
          RoleType.USER.getCode(),
          new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        //기존 사용하던 쿠키 삭제 후 새 토큰 저장
        int cookieMaxAgeForAccess = (int) appProperties.getAuth().getTokenExpiry() / 1000;
        CookieUtil.deleteCookie(request, response, ACCESS_TOKEN);
        CookieUtil.addCookieForAccess(
          response, ACCESS_TOKEN, newAccessToken.getToken(), cookieMaxAgeForAccess);

        return ApiResponse.success("access_token", newAccessToken);
      }
      return ApiResponse.notEqualRefreshToken();
    }
    return ApiResponse.expiredRefreshToken();
  }
}
