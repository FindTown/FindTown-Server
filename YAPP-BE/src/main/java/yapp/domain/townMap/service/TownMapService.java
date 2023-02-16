package yapp.domain.townMap.service;

import static yapp.common.config.Const.NON_USER;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yapp.common.domain.Location;
import yapp.common.repository.LocationRepository;
import yapp.domain.member.entity.MemberWishTown;
import yapp.domain.member.repository.MemberWishTownRepository;
import yapp.domain.townMap.converter.LocationConverter;
import yapp.domain.townMap.converter.PlaceConverter;
import yapp.domain.townMap.dto.InfraPlaceDto;
import yapp.domain.townMap.dto.response.InfraPlaceResponse;
import yapp.domain.townMap.dto.response.LocationInfoResponse;
import yapp.domain.townMap.dto.ThemePlaceDto;
import yapp.domain.townMap.dto.response.ThemePlaceResponse;
import yapp.domain.townMap.entity.Infra;
import yapp.domain.townMap.repository.InfraRepository;
import yapp.domain.townMap.repository.PlaceRepository;
import yapp.domain.townMap.repository.ThemeRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class TownMapService {

  private final LocationRepository locationRepository;
  private final MemberWishTownRepository memberWishTownRepository;
  private final PlaceRepository placeRepository;
  private final LocationConverter locationConverter;
  private final PlaceConverter placeConverter;

  public LocationInfoResponse getLocationInfo(
    String memberId,
    Optional<String> objectId
  ) {

    Location location;

    if (!objectId.isEmpty()) {

      location = this.locationRepository.getLocationByObjectId(
          Long.valueOf(objectId.get()))
        .orElseThrow();

    }
    else {
      try {

        List<Location> memberWishTownList = this.memberWishTownRepository.getMemberWishTownsByMemberId(
            memberId)
          .stream().map(MemberWishTown::getLocation).collect(Collectors.toList());

        Collections.shuffle(memberWishTownList);
        location = memberWishTownList.get(0);

      } catch (Exception e) {

        location = this.locationRepository.getLocationByObjectId(365L)
          .orElseThrow();

      }
    }

    return locationConverter.toLocationInfo(location).get();
  }

  public HashMap<String, List<InfraPlaceResponse>> getInfraPlaceInfo(
    Long object_id,
    String category
  ) {

    HashMap<String, List<InfraPlaceResponse>> infraPlaceListHashMap = new HashMap<>();

    List<InfraPlaceResponse> infraPlaceList = placeRepository.findByInfra(object_id, category)
      .stream()
      .map(placeConverter::toInfraPlace)
      .collect(Collectors.toList());

    infraPlaceListHashMap.put("placeList", infraPlaceList);

    return infraPlaceListHashMap;
  }

  public HashMap<String, List<ThemePlaceResponse>> getThemePlaceInfo(
    Long object_id,
    String category
  ) {

    HashMap<String, List<ThemePlaceResponse>> themePlaceListHashMap = new HashMap<>();

    List<ThemePlaceResponse> themePlaceList = placeRepository.findByTheme(object_id, category)
      .stream()
      .map(placeConverter::toThemePlace)
      .collect(Collectors.toList());

    themePlaceListHashMap.put("placeList", themePlaceList);

    return themePlaceListHashMap;
  }

}
