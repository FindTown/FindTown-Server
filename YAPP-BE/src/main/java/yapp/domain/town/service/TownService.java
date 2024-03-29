package yapp.domain.town.service;

import static yapp.common.config.Const.DIVIDE_NUM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yapp.common.repository.LocationRepository;
import yapp.domain.member.entity.WishStatus;
import yapp.domain.member.repository.MemberWishTownRepository;
import yapp.domain.town.comparator.TownComparator;
import yapp.domain.town.converter.TownConverter;
import yapp.domain.town.dto.TownDetailDto;
import yapp.domain.town.dto.TownDto;
import yapp.domain.town.dto.request.TownFilterRequest;
import yapp.domain.town.dto.response.TownFilterResponse;
import yapp.domain.town.dto.response.TownInfoResponse;
import yapp.domain.town.dto.response.TownSearchResponse;
import yapp.domain.town.entity.FilterStatus;
import yapp.domain.town.entity.Town;
import yapp.domain.town.repository.TownCustomRepository;
import yapp.domain.town.repository.TownMoodRepository;
import yapp.exception.base.town.TownException.TownNotFound;

@Slf4j
@Service
public class TownService {

  private final MemberWishTownRepository memberWishTownRepository;
  private final TownCustomRepository townCustomRepository;
  private final TownMoodRepository townMoodRepository;
  private final LocationRepository locationRepository;
  private final TownConverter townConverter;

  public TownService(
          MemberWishTownRepository memberWishTownRepository,
          TownCustomRepository townCustomRepository,
          TownMoodRepository townMoodRepository,
          LocationRepository locationRepository,
          TownConverter townConverter
  ) {
    this.memberWishTownRepository = memberWishTownRepository;
    this.townCustomRepository = townCustomRepository;
    this.townMoodRepository = townMoodRepository;
    this.locationRepository = locationRepository;
    this.townConverter = townConverter;
  }

  // 동네 필터
  @Transactional(readOnly = true)
  public List<TownFilterResponse> getTownFilter(
          Optional<String> memberId,
          TownFilterRequest townFilterRequest
  ) {
    //인프라(편의시설, 운동, 의료시설, 녹지공간), 교통(호선 정보들), 회원 정보(찜 목록을 위한)  request
    Set<Long> memberWishTownList = new HashSet<>();

    // 1. 회원 정보가 있다면 찜목록 상태 변경을 해준다. -> memberId 당 object_id 리스트 조회
    memberWishTownList = getMemberWishTownList(memberId, memberWishTownList);

    // 2. 인프라, 교통으로 동네 조회
    List<TownDto> townFilterList = townCustomRepository.getTownFilterList(
            townFilterRequest.getFilterStatus(), townFilterRequest.getSubwayList());

    // 2.1 동네 데이터 필터링
    // - 동네별로 그룹화
    Map<Long, TownDto> townDataMap = new HashMap<>();
    townFilterList.forEach(
            townData -> {
              if (townDataMap.containsKey(townData.getObjectId())) {
                townDataMap.get(townData.getObjectId())
                        .getPlaceSet()
                        .add(townData.getPlace());
                townDataMap.get(townData.getObjectId())
                        .getTownSubwaySet()
                        .add(townData.getTownSubway());
              } else {
                // 2.2 townDto에 있는 objectId를 기준으로 상위 2개 동네 분위기를 조회한다.
                String[] moods = getTownMoods(townData.getObjectId());
                townData.setMoods(moods);
                townData.setTownSubwaySet(new HashSet<>());
                townData.setPlaceSet(new HashSet<>());
                townData.setSafetyRate(
                        (townData.getTrafficRate() + townData.getLifeRate()) / DIVIDE_NUM);
                townDataMap.put(townData.getObjectId(), townData);
              }
            }
    );

    // - filter type별 정렬
    townFilterList = new ArrayList<>(townDataMap.values());
    filterInfraType(townFilterList, townFilterRequest.getFilterStatus());

    // - 찜 상태 등록
    Set<Long> finalMemberWishTownList = memberWishTownList;
    return townFilterList.stream()
            .map(town -> townConverter.toFilterTown(town, finalMemberWishTownList))
            .collect(Collectors.toList());
  }

  private String[] getTownMoods(Long objectId) {
    return this.townMoodRepository.findTop2ByTownObjectIdOrderByCntDesc(objectId)
            .stream()
            .map(t -> t.getMood().getKeyword())
            .toArray(String[]::new);
  }

  @Transactional(readOnly = true)
  public List<TownSearchResponse> getTownSearch(
          Optional<String> memberId,
          String searchType,
          String keyword
  ) {

    List<Town> townSearchList = townCustomRepository.getTownSearchList(searchType, keyword);

    //동네마다 분위기 상위 2개 조회
    Map<Long, String[]> townMoodsMap = townSearchList.stream()
            .collect(Collectors.toMap(Town::getObjectId, town -> getTownMoods(town.getObjectId())));

    Set<Long> memberWishTownList = new HashSet<>();
    memberWishTownList = getMemberWishTownList(memberId, memberWishTownList);

    Set<Long> finalMemberWishTownList = memberWishTownList;
    return townSearchList.stream()
            .map(town -> townConverter.toSearchTown(
                    town, finalMemberWishTownList, townMoodsMap.get(town.getObjectId())))
            .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public TownInfoResponse getTownDetailInfo(
          Optional<String> memberId,
          Long objectId
  ) {
    // 동내 조회
    Set<Long> memberWishTownList = new HashSet<>();

    // 1. 회원 정보가 있다면 찜목록 상태 변경을 해준다. -> memberId 당 object_id 리스트 조회
    memberWishTownList = getMemberWishTownList(memberId, memberWishTownList);

    // 2. 동네 데이터 조회
    List<TownDetailDto> townDetailDto = this.townCustomRepository.getTownDetailInfo(objectId);
    if (townDetailDto.isEmpty()) {
      throw new TownNotFound("아직 동네가 오픈되지 않았습니다.");
    }

    return townConverter.toTownDetailInfo(townDetailDto.get(0), memberWishTownList);
  }

  private Set<Long> getMemberWishTownList(
          Optional<String> memberId,
          Set<Long> memberWishTownList
  ) {
    if (memberId.isPresent()) {
      memberWishTownList = memberWishTownRepository.getMemberWishTownsByMemberIdAndWishStatus(
                      memberId.get(), WishStatus.YES)
              .stream()
              .map(town -> town.getLocation().getObjectId())
              .collect(Collectors.toSet());
    }
    return memberWishTownList;
  }

  private void filterInfraType(
          List<TownDto> townDtoList,
          FilterStatus filterStatus
  ) {
    switch (filterStatus) {
      case MEDICAL_FACILITY_FILTER:
        townDtoList.stream()
                .sorted(TownComparator.compareMedicalFilter)
                .collect(Collectors.toList());
      case EXERCISE_FILTER:
        townDtoList.stream()
                .sorted(TownComparator.compareTownFilter)
                .collect(Collectors.toList());
      case CONVENIENCE_FILTER:
        townDtoList.stream()
                .sorted(TownComparator.compareTownFilter)
                .collect(Collectors.toList());
      case GREENERY_FILTER:
        townDtoList.stream()
                .sorted(TownComparator.compareTownFilter)
                .collect(Collectors.toList());
      default:
    }
  }
}
