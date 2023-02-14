package yapp.common.oauth.handler;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;
import static yapp.common.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import yapp.common.config.AppProperties;
import yapp.common.oauth.entity.OAuth2UserInfo;
import yapp.common.oauth.entity.OAuth2UserInfoFactory;
import yapp.common.oauth.entity.ProviderType;
import yapp.common.oauth.entity.RoleType;
import yapp.common.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import yapp.common.oauth.token.AuthToken;
import yapp.common.oauth.token.AuthTokenProvider;
import yapp.common.utils.CookieUtil;
import yapp.domain.member.entity.MemberRefreshToken;
import yapp.domain.member.repository.MemberRefreshTokenRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final AuthTokenProvider tokenProvider;
  private final AppProperties appProperties;
  private final MemberRefreshTokenRepository memberRefreshTokenRepository;
  private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;

  @Override
  public void onAuthenticationSuccess(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
  ) throws IOException, ServletException {
    String targetUrl = determineTargetUrl(request, response, authentication);

    if (response.isCommitted()) {
      logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
      return;
    }

    clearAuthenticationAttributes(request, response);
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }

  protected String determineTargetUrl(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
  ) {
    Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
      .map(Cookie::getValue);

    if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
      throw new IllegalArgumentException(
        "Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
    }

    log.info("로그인 데이터를 받고 여기로 오는 경우는 없을 거 같다!");

    String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

    OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
    ProviderType providerType = ProviderType.valueOf(
      authToken.getAuthorizedClientRegistrationId().toUpperCase());

    OidcUser user = ((OidcUser) authentication.getPrincipal());

    // 애플 로그인 추가시 여기도 provider에 따라 나눠 주는 로직 추가!
    OAuth2UserInfo memberInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
      providerType, user.getAttributes());
    Collection<? extends GrantedAuthority> authorities = ((OidcUser) authentication.getPrincipal()).getAuthorities();

    RoleType roleType =
      hasAuthority(authorities, RoleType.ADMIN.getCode()) ? RoleType.ADMIN : RoleType.USER;

    Date now = new Date();
    AuthToken accessToken = tokenProvider.createAuthToken(
      memberInfo.getMemberId(),
      roleType.getCode(),
      new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
    );

    log.info(
      "[determineTargetUrl] accessToken!! id, expiredTokenClaims 생성 : {}, {}, {}",
      accessToken.getToken(), accessToken.getId(), accessToken.getExpiredTokenClaims()
    );

    // refresh 토큰 설정
    long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

    AuthToken refreshToken = tokenProvider.createAuthToken(
      appProperties.getAuth().getTokenSecret(),
      new Date(now.getTime() + refreshTokenExpiry)
    );
    log.info("access 토큰 정보 : {}", accessToken.getToken());
    log.info("refresh 토큰 정보 : {}", refreshToken.getToken());

    log.info(
      "[determineTargetUrl] refreshToken!! id, expiredTokenClaims 생성 : {}, {}, {}",
      refreshToken.getToken(), refreshToken.getId(), refreshToken.getExpiredTokenClaims()
    );

    // DB 저장
    MemberRefreshToken memberRefreshToken = memberRefreshTokenRepository.findByMemberId(
      memberInfo.getMemberId());

    log.info(
      "[determineTargetUrl - member 조회] : {}, {}, {}", memberInfo.getMemberId(),
      memberInfo.getEmail(), memberInfo.getNickname()
    );

    if (memberRefreshToken != null) {
      memberRefreshToken.setRefreshToken(refreshToken.getToken());
    } else {
      memberRefreshToken = new MemberRefreshToken(
        memberInfo.getMemberId(), refreshToken.getToken());
    }
    // refresh 토큰 upsert
    memberRefreshTokenRepository.saveAndFlush(memberRefreshToken);

    int cookieMaxAge = (int) refreshTokenExpiry / 60;
    int cookieMaxAgeForAccess = (int) appProperties.getAuth().getTokenExpiry() / 1000;

    /*
    Access Token 저장
     */
    CookieUtil.deleteCookie(request, response, ACCESS_TOKEN);
    CookieUtil.addCookieForAccess(
      response, ACCESS_TOKEN, accessToken.getToken(), cookieMaxAgeForAccess);

    /*
    Refresh Token 저장
     */
    CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
    CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

    return UriComponentsBuilder.fromUriString(targetUrl)
      .queryParam("token", accessToken.getToken())
      .queryParam("error", "")
      .build().toUriString();
  }

  protected void clearAuthenticationAttributes(
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    super.clearAuthenticationAttributes(request);
    authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
  }

  private boolean hasAuthority(
    Collection<? extends GrantedAuthority> authorities,
    String authority
  ) {
    if (authorities == null) {
      return false;
    }

    for (GrantedAuthority grantedAuthority : authorities) {
      if (authority.equals(grantedAuthority.getAuthority())) {
        return true;
      }
    }
    return false;
  }

  private boolean isAuthorizedRedirectUri(String uri) {
    URI clientRedirectUri = URI.create(uri);

    return appProperties.getOauth2().getAuthorizedRedirectUris()
      .stream()
      .anyMatch(authorizedRedirectUri -> {
        URI authorizedURI = URI.create(authorizedRedirectUri);
        return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
          && authorizedURI.getPort() == clientRedirectUri.getPort();
      });
  }
}
