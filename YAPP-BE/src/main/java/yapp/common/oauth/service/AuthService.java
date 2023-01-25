package yapp.common.oauth.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yapp.common.config.AppProperties;
import yapp.common.config.Const;
import yapp.common.oauth.token.AuthToken;
import yapp.common.oauth.token.AuthTokenProvider;
import yapp.domain.member.dto.request.MemberSignInRequest;
import yapp.domain.member.entitiy.Member;
import yapp.domain.member.entitiy.MemberPrincipal;
import yapp.domain.member.entitiy.MemberRefreshToken;
import yapp.domain.member.repository.MemberRefreshTokenRepository;
import yapp.domain.member.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
public class AuthService {

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
  public Map<String, String> login(MemberSignInRequest memberSignInRequest) {

    Optional<Member> member = this.memberRepository.findByMemberId(
      memberSignInRequest.getMemberId());

    if (member.isEmpty()) {
      return Map.of("register_check", String.valueOf(Const.NON_MEMBERS));
    }

    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
      member.get().getMemberId(),
      Const.DEFAULT_PASSWORD
    );

    Authentication authentication = authenticationManager.authenticate(token);

    SecurityContextHolder.getContext().setAuthentication(authentication);

    Date now = new Date();
    AuthToken accessToken = getAuthToken(member.get().getMemberId(), authentication);

    long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
    int cookieMaxAge = (int) refreshTokenExpiry / 60;

    AuthToken refreshToken = getRefreshToken(now, refreshTokenExpiry);

    MemberRefreshToken memberRefreshToken = memberRefreshTokenRepository.findByMemberId(
      member.get().getMemberId());

    extracted(member.get().getMemberId(), refreshToken, memberRefreshToken);

    return getStringStringMap(
      accessToken, refreshTokenExpiry, cookieMaxAge, refreshToken);
  }

  @NotNull
  private static Map<String, String> getStringStringMap(
    AuthToken accessToken,
    long refreshTokenExpiry,
    int cookieMaxAge,
    AuthToken refreshToken
  ) {
    Map<String, String> result = new HashMap<>();
    result.put("refresh_token_expiry", String.valueOf(refreshTokenExpiry));
    result.put("register_check", String.valueOf(Const.USE_MEMBERS));
    result.put("access_token", accessToken.getToken());
    result.put("refresh_token", refreshToken.getToken());
    result.put("cookie_max_age", String.valueOf(cookieMaxAge));
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
