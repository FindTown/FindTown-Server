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
import yapp.domain.member.repository.MemberWishTownRepository;
import yapp.domain.town.comparator.TownComparator;
import yapp.domain.town.converter.TownConverter;
import yapp.domain.town.dto.TownDto;
import yapp.domain.town.dto.request.TownFilterRequest;
import yapp.domain.town.dto.request.TownSearchRequest;
import yapp.domain.town.dto.response.TownFilterResponse;
import yapp.domain.town.dto.response.TownSearchResponse;
import yapp.domain.town.entity.FilterStatus;
import yapp.domain.town.entity.Town;
import yapp.domain.town.repository.TownCustomRepository;

@Slf4j
@Service
public class TownService {

  private final MemberWishTownRepository memberWishTownRepository;
  private final TownCustomRepository townCustomRepository;
  private final TownConverter townConverter;

  public TownService(
    MemberWishTownRepository memberWishTownRepository,
    TownCustomRepository townCustomRepository,
    TownConverter townConverter
  ) {
    this.memberWishTownRepository = memberWishTownRepository;
    this.townCustomRepository = townCustomRepository;
    this.townConverter = townConverter;
  }

  // 동네 필터
  public List<TownFilterResponse> getTownFilter(
    Optional<String> memberId,
    TownFilterRequest townFilterRequest
  ) {
    //인프라(편의시설, 운동, 의료시설, 녹지공간), 교통(호선 정보들), 회원 정보(찜 목록을 위한)  request
    Set<Long> memberWishTownList = new HashSet<>();

    // 1. 회원 정보가 있다면 찜목록 상태 변경을 해준다. -> memberId 당 object_id 리스트 조회
    if (memberId.isPresent()) {
      memberWishTownList = memberWishTownRepository.getMemberWishTownsByMemberId(
          memberId.get())
        .stream().map(town -> town.getLocation().getObjectId()).collect(Collectors.toSet());
    }

    // 2. 인프라, 교통으로 동네 조회
    List<TownDto> townFilterList = townCustomRepository.getTownFilterList(
      townFilterRequest.getFilterStatus(), townFilterRequest.getSubwayList());

    // 2.5 동네 데이터 필터링
    // - 동네별로 그룹화 
    Map<Long, TownDto> townDataMap = new HashMap<>();
    townFilterList.forEach(
      townData -> {
        if (townDataMap.containsKey(townData.getObjectId())) {
          townDataMap.get(townData.getObjectId()).getPlaceSet().add(townData.getPlace());
          townDataMap.get(townData.getObjectId()).getTownSubwaySet().add(townData.getTownSubway());
        } else {
          townData.setTownSubwaySet(new HashSet<>());
          townData.setPlaceSet(new HashSet<>());
          townData.setSafetyRate((townData.getTrafficRate() + townData.getLifeRate()) / DIVIDE_NUM);
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

  public List<TownSearchResponse> getTownSearch(
    Optional<String> memberId,
    TownSearchRequest townSearchRequest
  ){

    List<Town> townSearchList = townCustomRepository.getTownSearchList(
      townSearchRequest.getSggNm());

    Set<Long> memberWishTownList = new HashSet<>();

    if (memberId.isPresent()) {
      memberWishTownList = memberWishTownRepository.getMemberWishTownsByMemberId(
          memberId.get())
        .stream().map(town -> town.getLocation().getObjectId()).collect(Collectors.toSet());
    }

    Set<Long> finalMemberWishTownList = memberWishTownList;

    return townSearchList.stream()
      .map(town -> townConverter.toSearchTown(town, finalMemberWishTownList))
      .collect(Collectors.toList());
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
        townDtoList.stream().sorted(TownComparator.compareTownFilter).collect(Collectors.toList());
      case CONVENIENCE_FILTER:
        townDtoList.stream().sorted(TownComparator.compareTownFilter).collect(Collectors.toList());
      case GREENERY_FILTER:
        townDtoList.stream().sorted(TownComparator.compareTownFilter).collect(Collectors.toList());
      default:
    }
  }
}
