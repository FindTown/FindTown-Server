package yapp.common.oauth.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yapp.common.config.AppProperties;
import yapp.common.config.Const;
import yapp.common.oauth.provider.AuthProvider;
import yapp.common.oauth.token.AuthToken;
import yapp.common.oauth.token.AuthTokenProvider;
import yapp.domain.member.entity.Member;
import yapp.domain.member.entity.MemberPrincipal;
import yapp.domain.member.entity.MemberRefreshToken;
import yapp.domain.member.repository.MemberRefreshTokenRepository;
import yapp.domain.member.repository.MemberRepository;
import yapp.exception.base.member.MemberException.MemberNotFound;

@Service
@Transactional(readOnly = true)
public class AuthService implements AuthProvider {

  private final AppProperties appProperties;
  private final AuthTokenProvider authTokenProvider;
  private final MemberRepository memberRepository;
  private final AuthenticationManager authenticationManager;
  private final MemberRefreshTokenRepository memberRefreshTokenRepository;

  public AuthService(
    AppProperties appProperties,
    AuthTokenProvider authTokenProvider,
    MemberRepository memberRepository,
    AuthenticationManager authenticationManager,
    MemberRefreshTokenRepository memberRefreshTokenRepository
  ) {
    this.appProperties = appProperties;
    this.authTokenProvider = authTokenProvider;
    this.memberRepository = memberRepository;
    this.authenticationManager = authenticationManager;
    this.memberRefreshTokenRepository = memberRefreshTokenRepository;
  }

  @Transactional
  @Override
  public Map<String, Object> login(String memberId) {

    Member member = this.memberRepository.findByMemberId(
        memberId)
      .orElseThrow(() -> {throw new MemberNotFound("가입된 회원을 찾을수 없습니다.");});

    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
      member.getMemberId(),
      Const.DEFAULT_PASSWORD
    );

    Authentication authentication = authenticationManager.authenticate(token);

    SecurityContextHolder.getContext().setAuthentication(authentication);

    Date now = new Date();
    AuthToken accessToken = getAuthToken(member.getMemberId(), authentication);

    long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
    long accessTokenExpiry = appProperties.getAuth().getTokenExpiry();
    int cookieMaxAge = (int) refreshTokenExpiry / 60;
    int cookieMaxAgeForAccess = (int) appProperties.getAuth().getTokenExpiry() / 1000;

    AuthToken refreshToken = getRefreshToken(now, refreshTokenExpiry);

    MemberRefreshToken memberRefreshToken = memberRefreshTokenRepository.findByMemberId(
      member.getMemberId());

    extracted(member.getMemberId(), refreshToken, memberRefreshToken);

    return getStringStringMap(
      accessToken, accessTokenExpiry, refreshTokenExpiry, cookieMaxAge, cookieMaxAgeForAccess,
      refreshToken
    );
  }

  @NotNull
  private static Map<String, Object> getStringStringMap(
    AuthToken accessToken,
    long accessTokenExpiry,
    long refreshTokenExpiry,
    int cookieMaxAge,
    int cookieMaxAgeForAccess,
    AuthToken refreshToken
  ) {
    Map<String, Object> result = new HashMap<>();
    result.put("access_token_expiry", accessTokenExpiry);
    result.put("refresh_token_expiry", refreshTokenExpiry);
    result.put("access_token", accessToken.getToken());
    result.put("refresh_token", refreshToken.getToken());
    result.put("cookie_max_age", cookieMaxAge);
    result.put("cookie_max_age_for_access", cookieMaxAgeForAccess);
    return result;
  }

  private void extracted(
    String memberId,
    AuthToken refreshToken,
    MemberRefreshToken memberRefreshToken
  ) {
    if (memberRefreshToken == null) {
      memberRefreshToken = new MemberRefreshToken(memberId, refreshToken.getToken());
      this.memberRefreshTokenRepository.saveAndFlush(memberRefreshToken);
    } else {
      memberRefreshToken.setRefreshToken(refreshToken.getToken());
      this.memberRefreshTokenRepository.saveAndFlush(memberRefreshToken);
    }
  }

  private AuthToken getRefreshToken(
    Date now,
    long refreshTokenExpiry
  ) {
    return authTokenProvider.createAuthToken(
      appProperties.getAuth().getTokenSecret(),
      new Date(now.getTime() + refreshTokenExpiry)
    );
  }

  private AuthToken getAuthToken(
    String memberId,
    Authentication authentication
  ) {
    Date now = new Date();
    String code = ((MemberPrincipal) authentication.getPrincipal()).getRoleType().getCode();
    Date expired = new Date(now.getTime() + appProperties.getAuth().getTokenExpiry());

    return this.authTokenProvider.createAuthToken(memberId, code, expired);
  }
}
