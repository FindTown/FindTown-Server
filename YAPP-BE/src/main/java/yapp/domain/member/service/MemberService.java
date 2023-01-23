package yapp.domain.member.service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yapp.common.config.Const;
import yapp.common.domain.Location;
import yapp.common.oauth.token.AuthTokenProvider;
import yapp.common.repository.LocationRepository;
import yapp.domain.member.converter.MemberConverter;
import yapp.domain.member.dto.request.MemberSignUpRequest;
import yapp.domain.member.dto.response.MemberInfoResponse;
import yapp.domain.member.entitiy.Member;
import yapp.domain.member.entitiy.MemberWishTown;
import yapp.domain.member.entitiy.WishStatus;
import yapp.domain.member.repository.MemberRefreshTokenRepository;
import yapp.domain.member.repository.MemberRepository;
import yapp.domain.member.repository.MemberWishTownRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MemberService {
  private final MemberRepository memberRepository;
  private final MemberWishTownRepository memberWishTownRepository;
  private final MemberRefreshTokenRepository memberRefreshTokenRepository;
  private final LocationRepository locationRepository;
  private final MemberConverter memberConverter;
  private final AuthTokenProvider authTokenProvider;
  private final RedisTemplate<String, String> redisTemplate;

  public MemberService(
    MemberRepository memberRepository,
    MemberWishTownRepository memberWishTownRepository,
    MemberRefreshTokenRepository memberRefreshTokenRepository,
    LocationRepository locationRepository,
    MemberConverter memberConverter,
    AuthTokenProvider authTokenProvider,
    RedisTemplate<String, String> redisTemplate
  ) {
    this.memberRepository = memberRepository;
    this.memberWishTownRepository = memberWishTownRepository;
    this.memberRefreshTokenRepository = memberRefreshTokenRepository;
    this.locationRepository = locationRepository;
    this.memberConverter = memberConverter;
    this.authTokenProvider = authTokenProvider;
    this.redisTemplate = redisTemplate;
  }

  public MemberInfoResponse getMemberInfo(String memberId) {
    Member member = this.memberRepository.findByMemberIdAndUseStatus(memberId, Const.USE_MEMBERS)
      .orElseThrow(() -> new UsernameNotFoundException("현재 사용중인 회원이 아닙니다"));

    List<Location> memberWishTownList = this.memberWishTownRepository.getMemberWishTownsByMemberId(
      memberId).stream().map(MemberWishTown::getLocation).collect(Collectors.toList());

    log.info("회원 ID : {}", member.getMemberId());
    log.info("찜 목록 조회 : {}", memberWishTownList.size());
    return memberConverter.toMemberInfo(member, memberWishTownList).get();
  }

  @Transactional
  public String memberSignUp(
    MemberSignUpRequest memberSignUpRequest,
    String memberId
  ) {
    Member signUpMember = this.memberRepository.findByMemberId(memberId)
      .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

    signUpMember.setSignUp(memberSignUpRequest);
    signUpMember.changeMemberStatus(Const.USE_MEMBERS);

    if (memberSignUpRequest.getObjectId() != null) {
      Location location = this.locationRepository.getLocationByObjectId(
          memberSignUpRequest.getObjectId())
        .orElseThrow(() -> new RuntimeException("입력한 동네는 현재 존재하지 않습니다."));
      this.memberWishTownRepository.save(new MemberWishTown(memberId, location, WishStatus.YES));
    }

    return this.memberRepository.save(signUpMember).getMemberId();
  }

  public int checkRegister(
    String memberId
  ) {
    Member member = this.memberRepository.findByMemberId(memberId)
      .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
    return member.getUseStatus();
  }

  @Transactional
  public void memberLogout(
    String accessToken,
    String memberId
  ) {
    // 1. DB refresh 토큰 삭제하기
    this.memberRefreshTokenRepository.deleteByMemberId(memberId);

    // 2. redis에 access_token 등록
    Long expiration = authTokenProvider.getExpiration(accessToken);
    redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
  }

  public boolean checkDuplicateNickname(String nickname) {
    return this.memberRepository.existsAllByNickname(nickname);
  }
}
