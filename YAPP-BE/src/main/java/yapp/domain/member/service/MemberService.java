package yapp.domain.member.service;

import static yapp.common.config.Const.MAX_RETRY;
import static yapp.domain.member.entity.WishStatus.NO;
import static yapp.domain.member.entity.WishStatus.YES;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
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
import yapp.domain.member.converter.MemberWishTownConverter;
import yapp.domain.member.dto.MemberWishTownDto;
import yapp.domain.member.dto.request.MemberSignUpRequest;
import yapp.domain.member.dto.response.MemberInfoResponse;
import yapp.domain.member.entity.Member;
import yapp.domain.member.entity.MemberRefreshToken;
import yapp.domain.member.entity.MemberWishTown;
import yapp.domain.member.repository.MemberRefreshTokenRepository;
import yapp.domain.member.repository.MemberRepository;
import yapp.domain.member.repository.MemberWishTownRepository;
import yapp.domain.town.converter.TownResidentConverter;
import yapp.domain.town.entity.Mood;
import yapp.domain.town.entity.Town;
import yapp.domain.town.entity.TownMood;
import yapp.domain.town.entity.TownResident;
import yapp.domain.town.repository.MoodRepository;
import yapp.domain.town.repository.TownCustomRepository;
import yapp.domain.town.repository.TownMoodRepository;
import yapp.domain.town.repository.TownRepository;
import yapp.domain.town.repository.TownResidentRepositroy;
import yapp.exception.base.member.MemberException.DuplicateMember;
import yapp.exception.base.member.MemberException.MemberNotFound;
import yapp.exception.base.member.MemberException.MemberSignUpFail;
import yapp.exception.base.member.MemberException.NickNameDuplicated;
import yapp.exception.base.town.TownException.MoodNotFound;
import yapp.exception.base.town.TownException.TownNotFound;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MemberService {
  private final AuthProvider authProvider;
  private final MemberRepository memberRepository;
  private final TownMoodRepository townMoodRepository;
  private final MemberWishTownRepository memberWishTownRepository;
  private final MemberRefreshTokenRepository memberRefreshTokenRepository;
  private final LocationRepository locationRepository;
  private final TownResidentRepositroy townResidentRepositroy;
  private final MoodRepository moodRepository;
  private final MemberConverter memberConverter;
  private final TownResidentConverter townResidentConverter;
  private final AuthTokenProvider authTokenProvider;
  private final RedisTemplate<String, String> redisTemplate;
  private final TownRepository townRepository;
  private final TownCustomRepository townCustomRepository;
  private final MemberWishTownConverter memberWishTownConverter;

  public MemberService(
          AuthProvider authProvider,
          MemberRepository memberRepository,
          TownMoodRepository townMoodRepository,
          MemberWishTownRepository memberWishTownRepository,
          MemberRefreshTokenRepository memberRefreshTokenRepository,
          LocationRepository locationRepository,
          TownResidentRepositroy townResidentRepositroy,
          MoodRepository moodRepository,
          MemberConverter memberConverter,
          TownResidentConverter townResidentConverter,
          AuthTokenProvider authTokenProvider,
          RedisTemplate<String, String> redisTemplate,
          TownRepository townRepository,
          TownCustomRepository townCustomRepository,
          MemberWishTownConverter memberWishTownConverter
  ) {
    this.authProvider = authProvider;
    this.memberRepository = memberRepository;
    this.townMoodRepository = townMoodRepository;
    this.memberWishTownRepository = memberWishTownRepository;
    this.memberRefreshTokenRepository = memberRefreshTokenRepository;
    this.locationRepository = locationRepository;
    this.townResidentRepositroy = townResidentRepositroy;
    this.moodRepository = moodRepository;
    this.memberConverter = memberConverter;
    this.townResidentConverter = townResidentConverter;
    this.authTokenProvider = authTokenProvider;
    this.redisTemplate = redisTemplate;
    this.townRepository = townRepository;
    this.townCustomRepository = townCustomRepository;
    this.memberWishTownConverter = memberWishTownConverter;
  }

  public MemberInfoResponse getMemberInfo(String memberId) {
    Member member = this.memberRepository.findByMemberIdAndUseStatus(
                    memberId, Const.USE_MEMBERS)
            .orElseThrow(() -> new UsernameNotFoundException("현재 사용중인 회원이 아닙니다"));

    List<TownResident> townResident = this.townResidentRepositroy.findTownResidentByMemberId(
            memberId);
    List<Location> memberWishTownList = this.memberWishTownRepository.getMemberWishTownsByMemberIdAndWishStatus(
                    memberId, YES)
            .stream()
            .map(MemberWishTown::getLocation)
            .collect(Collectors.toList());

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

        /* todo : town_mood에 동네 분위기 입력/수정 로직 추가
                 - '낙관적 락'으로 동시성을 제어한다.
                 1. find 진행
                     1-1 이미 존재하면 -> cnt++
                     1-2 없으면 TownMood 객체를 만든다. -> 새로 저장
                 2. lock 예외 로직 -> 새로 find로 조회한다음 cnt++한다.
         */
    String[] selectMoods = memberSignUpRequest.getResident().getMoods();
    Long finalResidentObjectId = residentObjectId;
    Arrays.stream(selectMoods).forEach(mood -> {
      updateTownMoodCntWithRetry(finalResidentObjectId, mood);
    });

    String memberId;
    this.townResidentRepositroy.save(insertTownResident);
    try {
      memberId = this.memberRepository.save(signUpMember).getMemberId();
    } catch (Exception e) {
      throw new MemberSignUpFail("회원 가입에 실패하셨습니다.");
    }

    return authProvider.login(memberId);
  }

  @Transactional
  public void updateTownMoodCntWithRetry(
          Long objectId,
          String mood
  ) {
    int retryCnt = 0;
    while (retryCnt < MAX_RETRY) {
      try {
        updateTownMoodCnt(objectId, mood);
      } catch (ConcurrentModificationException e) {
        retryCnt++;
      }
    }
  }

  @Transactional
  public void updateTownMoodCnt(
          Long objectId,
          String mood
  ) {
    Optional<TownMood> getTownMood = this.townMoodRepository.findByTownObjectIdAndMoodKeyword(
            objectId, mood);
    if (getTownMood.isPresent()) {
      TownMood townMood = getTownMood.get();
      int currentVersion = townMood.getVersion();
      townMood.changeMoodCnt(townMood.getCnt() + 1);
      this.townMoodRepository.save(townMood);
      int newVersion = townMood.getVersion();
      if (newVersion != currentVersion + 1) {
        throw new ConcurrentModificationException("동시에 town_mood수정으로인한 에러발생");
      }
    } else {
      // Town 객체 조회, Mood 객체 조회 후 insert 진행
      Town getTown = this.townRepository.findTownByObjectId(objectId)
              .orElseThrow(() -> {throw new TownNotFound("해당 동네를 찾을 수 없습니다.");});
      Mood getMood = this.moodRepository.findByKeyword(mood)
              .orElseThrow(() -> {throw new MoodNotFound("해당 분위기를 찾을 수 없습니다.");});
      TownMood newTownMood = new TownMood(getTown, getMood, 1L);
      this.townMoodRepository.save(newTownMood);
    }
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

  public HashMap<String, List<MemberWishTownDto>> getMemberWishList(String memberId) {

    //0. 찜한 동네 조회
    List<Town> memberWishTownList = this.townCustomRepository.getMemberWishTownList(memberId);

    //1. 내가 찜한 동네 -> 분위기 조회
    Map<Long, String[]> townMoodsMap = memberWishTownList.stream()
            .collect(Collectors.toMap(
                    Town::getObjectId,
                    town -> townMoodRepository.findTop2ByTownObjectIdOrderByCntDesc(
                                    town.getObjectId())
                            .stream()
                            .map(t -> t.getMood().getKeyword())
                            .toArray(String[]::new)
            ));

    //2. 구 정보 출력
    Map<Long, Location> objectSggNmMap = this.locationRepository.getLocationsByObjectIdIn(
                    memberWishTownList.stream().map(Town::getObjectId).collect(Collectors.toList()))
            .stream().collect(Collectors.toMap(Location::getObjectId, location -> location));

    List<MemberWishTownDto> wishTownList = memberWishTownList
            .stream()
            .map(town -> memberWishTownConverter.toMemberWishTownDto(
                    town, townMoodsMap.get(town.getObjectId()),
                    objectSggNmMap.get(town.getObjectId()).getSggNm()
            ))
            .collect(Collectors.toList());

    HashMap<String, List<MemberWishTownDto>> wishTownListHashMap = new HashMap<>();
    wishTownListHashMap.put("townList", wishTownList);

    return wishTownListHashMap;
  }

  @Transactional
  public String setMemberWishTown(
          String objectId,
          String memberId
  ) {
    Location location = this.locationRepository.getLocationByObjectId(Long.valueOf(objectId))
            .orElseThrow();

    String msg = "찜 등록";

    try {
      MemberWishTown wishTown = this.memberWishTownRepository.getMemberWishTownByMemberIdAndLocation(
              memberId, location).orElseThrow();

      if (wishTown.getWishStatus().equals(YES)) {
        wishTown.changeWishStatus(NO);
        msg = "찜 해제";
      } else {
        wishTown.changeWishStatus(YES);
      }
    } catch (Exception e) {
      memberWishTownRepository.save(MemberWishTown.builder()
              .wishStatus(YES)
              .memberId(memberId)
              .location(location)
              .build());
    }

    return msg;
  }

}
