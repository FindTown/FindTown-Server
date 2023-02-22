package yapp.common.oauth.controller;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN;
import static yapp.common.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository.REFRESH_TOKEN;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yapp.common.config.AppProperties;
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
import yapp.domain.member.entity.MemberRefreshToken;
import yapp.domain.member.repository.MemberRefreshTokenRepository;
import yapp.exception.base.member.MemberException.MemberTokenExpired;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;
  private final AppProperties appProperties;
  private final AuthTokenProvider authTokenProvider;
  private final MemberRefreshTokenRepository memberRefreshTokenRepository;

  public AuthController(
    AuthService authService,
    AppProperties appProperties,
    AuthTokenProvider authTokenProvider,
    MemberRefreshTokenRepository memberRefreshTokenRepository
  ) {
    this.authService = authService;
    this.appProperties = appProperties;
    this.authTokenProvider = authTokenProvider;
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
    Map<String, Object> result = authService.login(memberSignInRequest.getMemberId());

    // 회원이 아닐시
    if (result.get("login_status").equals(Boolean.FALSE)) {
      return new ApiResponse(new ApiResponseHeader(200, "회원이 아닌 계정입니다."), result);
    }

    // access_token 담기
    CookieUtil.deleteCookie(request, response, ACCESS_TOKEN);
    CookieUtil.addCookieForAccess(
      response, ACCESS_TOKEN, String.valueOf(result.get("access_token")),
      (Integer) result.get("cookie_max_age_for_access")
    );

    // refresh_token 담기
    CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
    CookieUtil.addCookie(
      response, REFRESH_TOKEN, String.valueOf(result.get("refresh_token")),
      (Integer) result.get("cookie_max_age")
    );
    return new ApiResponse(new ApiResponseHeader(200, "회원 계정입니다."), result);
  }

  @PostMapping("/login/confirm")
  @Operation(summary = "access_token으로 회원 ID , 권한 정보 확인")
  @Tag(name = "[권한/인증]")
  public ApiResponse checkLoginConfirm(
    HttpServletRequest request,
    @CurrentAuthPrincipal User memberPrincipal
  ) {
    Map<String, Object> result = new HashMap<>();
    this.authService.confirmMember(HeaderUtil.getAccessToken(request));
    result.put("memberId", memberPrincipal.getUsername());
    result.put("authorities", memberPrincipal.getAuthorities());

    return ApiResponse.success("login_confirm", result);
  }

  @PostMapping("/reissue/token")
  @Operation(summary = "access_token 재발급")
  @Tag(name = "[권한/인증]")
  public ApiResponse reissueRefreshToken(
    @RequestHeader(REFRESH_TOKEN) String refreshToken,
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    Date now = new Date();
    AuthToken newAccessToken;
    AuthToken authRefreshToken = authTokenProvider.convertAuthToken(refreshToken);

    if (authRefreshToken.validate()) {

      Optional<MemberRefreshToken> memberRefreshToken = memberRefreshTokenRepository.findByRefreshToken(
        refreshToken);
      if (memberRefreshToken.isPresent()) {
        //새 토큰 발급
        newAccessToken = authTokenProvider.createAuthToken(
          memberRefreshToken.get().getMemberId(),
          RoleType.USER.getCode(),
          new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        //기존 사용하던 쿠키 삭제 후 새 토큰 저장
        int cookieMaxAgeForAccess = (int) appProperties.getAuth().getTokenExpiry() / 1000;
        CookieUtil.deleteCookie(request, response, ACCESS_TOKEN);
        CookieUtil.addCookieForAccess(
          response, ACCESS_TOKEN, newAccessToken.getToken(), cookieMaxAgeForAccess);
      } else {
        throw new MemberTokenExpired("REFRESH TOKEN 정보가 일치하지 않습니다.");
      }
    } else {
      throw new MemberTokenExpired("만료된 REFRESH TOKEN 입니다. 로그인을 다시 진행해주세요");
    }

    return ApiResponse.success("access_token", newAccessToken);
  }
}
