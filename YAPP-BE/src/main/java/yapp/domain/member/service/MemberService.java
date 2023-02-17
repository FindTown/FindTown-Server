package yapp.domain.member.service;

import static yapp.domain.member.entity.WishStatus.NO;
import static yapp.domain.member.entity.WishStatus.YES;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yapp.common.config.Const;
import yapp.common.domain.Location;
import yapp.common.oauth.provider.AuthProvider;
import yapp.common.oauth.token.AuthTokenProvider;
import yapp.common.repository.LocationRepository;
import yapp.domain.member.converter.MemberConverter;
import yapp.domain.member.dto.request.MemberSignUpRequest;
import yapp.domain.member.dto.response.MemberInfoResponse;
import yapp.domain.member.entity.Member;
import yapp.domain.member.entity.MemberRefreshToken;
import yapp.domain.member.entity.MemberWishTown;
import yapp.domain.member.repository.MemberRefreshTokenRepository;
import yapp.domain.member.repository.MemberRepository;
import yapp.domain.member.repository.MemberWishTownRepository;
import yapp.domain.town.converter.TownResidentConverter;
import yapp.domain.town.entity.TownResident;
import yapp.domain.town.repository.TownResidentRepositroy;
import yapp.exception.base.member.MemberException.DuplicateMember;
import yapp.exception.base.member.MemberException.MemberNotFound;
import yapp.exception.base.member.MemberException.MemberSignUpFail;
import yapp.exception.base.member.MemberException.NickNameDuplicated;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MemberService {
  private final AuthProvider authProvider;
  private final MemberRepository memberRepository;
  private final MemberWishTownRepository memberWishTownRepository;
  private final MemberRefreshTokenRepository memberRefreshTokenRepository;
  private final LocationRepository locationRepository;
  private final TownResidentRepositroy townResidentRepositroy;
  private final MemberConverter memberConverter;
  private final TownResidentConverter townResidentConverter;
  private final AuthTokenProvider authTokenProvider;
  private final RedisTemplate<String, String> redisTemplate;

  public MemberService(
    AuthProvider authProvider,
    MemberRepository memberRepository,
    MemberWishTownRepository memberWishTownRepository,
    MemberRefreshTokenRepository memberRefreshTokenRepository,
    LocationRepository locationRepository,
    TownResidentRepositroy townResidentRepositroy,
    MemberConverter memberConverter,
    TownResidentConverter townResidentConverter,
    AuthTokenProvider authTokenProvider,
    RedisTemplate<String, String> redisTemplate
  ) {
    this.authProvider = authProvider;
    this.memberRepository = memberRepository;
    this.memberWishTownRepository = memberWishTownRepository;
    this.memberRefreshTokenRepository = memberRefreshTokenRepository;
    this.locationRepository = locationRepository;
    this.townResidentRepositroy = townResidentRepositroy;
    this.memberConverter = memberConverter;
    this.townResidentConverter = townResidentConverter;
    this.authTokenProvider = authTokenProvider;
    this.redisTemplate = redisTemplate;
  }

  public MemberInfoResponse getMemberInfo(String memberId) {
    Member member = this.memberRepository.findByMemberIdAndUseStatus(memberId, Const.USE_MEMBERS)
      .orElseThrow(() -> new UsernameNotFoundException("현재 사용중인 회원이 아닙니다"));

    List<TownResident> townResident = this.townResidentRepositroy.findTownResidentByMemberId(
      memberId);
    List<Location> memberWishTownList = this.memberWishTownRepository.getMemberWishTownsByMemberId(
      memberId).stream().map(MemberWishTown::getLocation).collect(Collectors.toList());

    return memberConverter.toMemberInfo(
      member, memberWishTownList, townResident);
  }

  @Transactional
  public Map<String, Object> memberSignUp(
    MemberSignUpRequest memberSignUpRequest
  ) {
    //회원 체크
    duplicateMemberConfirm(memberSignUpRequest.getMemberId());

    Member signUpMember = this.memberConverter.toEntity(memberSignUpRequest);

    if (memberSignUpRequest.getObjectId() != null) {
      Location location = this.locationRepository.getLocationByObjectId(
          memberSignUpRequest.getObjectId())
        .orElseThrow(() -> new RuntimeException("입력한 동네는 현재 존재하지 않습니다."));
      this.memberWishTownRepository.save(
        new MemberWishTown(memberSignUpRequest.getMemberId(), location, YES));
    }

    Long residentObjectId = 0L;
    String[] residentAdmNm = memberSignUpRequest.getResident().getResidentAddress().split(" ");
    Optional<Location> residentLocation = this.locationRepository.getLocationByAdmNm(
      residentAdmNm[residentAdmNm.length - 1]);
    if (residentLocation.isPresent()) {
      residentObjectId = residentLocation.get().getObjectId();
    }
    TownResident insertTownResident = this.townResidentConverter.toEntity(
      memberSignUpRequest, residentObjectId);

    String memberId;
    this.townResidentRepositroy.save(insertTownResident);
    try {
      memberId = this.memberRepository.save(signUpMember).getMemberId();
    } catch (Exception e) {
      throw new MemberSignUpFail("회원 가입에 실패하셨습니다.");
    }

    return authProvider.login(memberId);
  }

  public void duplicateMemberConfirm(
    String memberId
  ) {
    this.memberRepository.findByMemberId(memberId)
      .map(member -> {throw new DuplicateMember("이미 가입된 회원입니다");});
  }

  @Transactional
  public void removeMember(
    String memberId,
    String accessToken
  ) {
    Member member = this.memberRepository.findByMemberId(memberId)
      .orElseThrow(() -> {throw new MemberNotFound("회원을 찾을 수 없습니다.");});
    List<TownResident> townResident = this.townResidentRepositroy.findTownResidentByMemberId(
      memberId);

    Long expiration = authTokenProvider.getExpiration(accessToken);
    redisTemplate.opsForValue()
      .set(accessToken, "account_withdrawal", expiration, TimeUnit.MILLISECONDS);

    if (!townResident.isEmpty()) {
      townResident.forEach(TownResident::removeMemberId);
      this.townResidentRepositroy.saveAll(townResident);
    }

    this.memberWishTownRepository.deleteMemberWishTownsByMemberId(memberId);
    this.memberRefreshTokenRepository.deleteByMemberId(memberId);
    this.memberRepository.delete(member);
  }

  @Transactional
  public void editNickname(
    String memberId,
    String newNickname
  ) {
    Member member = this.memberRepository.findByMemberId(memberId)
      .orElseThrow(() -> {throw new MemberNotFound("회원을 찾을 수 없습니다.");});

    boolean duplicateNickname = this.checkDuplicateNickname(newNickname);
    if (duplicateNickname) {
      throw new NickNameDuplicated("이미 등록된 닉네임 입니다.");
    }

    member.changeNickname(newNickname);
    this.memberRepository.save(member);
  }

  public int checkRegister(
    String memberId
  ) {
    Optional<Member> member = this.memberRepository.findByMemberId(memberId);
    if (member.isEmpty()) {
      return Const.NON_MEMBERS;
    }
    return member.get().getUseStatus();
  }

  @Transactional
  public void memberLogout(
    String accessToken,
    String memberId
  ) {
    // 1. DB refresh 토큰 공백으로 변경
    MemberRefreshToken memberRefreshToken = this.memberRefreshTokenRepository.findByMemberId(
      memberId);
    memberRefreshToken.setRefreshToken("");

    // 2. redis에 access_token 등록
    Long expiration = authTokenProvider.getExpiration(accessToken);
    redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

    this.memberRefreshTokenRepository.saveAndFlush(memberRefreshToken);
  }

  public boolean checkDuplicateNickname(String nickname) {
    return this.memberRepository.existsAllByNickname(nickname);
  }

  @Transactional
  public void setMemberWishTown(
    String objectId,
    String memberId
  ) {
    Location location = this.locationRepository.getLocationByObjectId(Long.valueOf(objectId))
      .orElseThrow();

    MemberWishTown wishTown = this.memberWishTownRepository.getMemberWishTownByMemberIdAndLocation(
      memberId, location).orElseThrow();

    if (wishTown != null) {

      if (wishTown.getWishStatus().equals(YES)) {
        wishTown.changeWishStatus(NO);
      } else {
        wishTown.changeWishStatus(YES);
      }
    } else {
      memberWishTownRepository.save(MemberWishTown.builder()
        .wishStatus(YES)
        .memberId(memberId)
        .location(location)
        .build());
    }
  }

}
