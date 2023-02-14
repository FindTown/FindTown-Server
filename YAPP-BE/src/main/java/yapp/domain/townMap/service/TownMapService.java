package yapp.domain.townMap.service;

import static yapp.common.config.Const.NON_USER;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import yapp.domain.townMap.dto.response.InfraPlaceDto;
import yapp.domain.townMap.dto.response.LocationInfoResponse;
import yapp.domain.townMap.dto.response.ThemePlaceDto;
import yapp.domain.townMap.entity.Infra;
import yapp.domain.townMap.entity.Theme;
import yapp.domain.townMap.repository.InfraRepository;
import yapp.domain.townMap.repository.PlaceRepository;
import yapp.domain.townMap.repository.ThemeRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class TownMapService {

  private final LocationRepository locationRepository;
  private final MemberWishTownRepository memberWishTownRepository;
  private final InfraRepository infraRepository;
  private final ThemeRepository themeRepository;
  private final PlaceRepository placeRepository;
  private final LocationConverter locationConverter;

  public LocationInfoResponse getLocationInfo(
    String memberId,
    Optional<String> objectId
  ) {

    Location location;

    if (!objectId.isEmpty()) {

      log.info("도달2-in");
      location = this.locationRepository.getLocationByObjectId(
          Long.valueOf(objectId.get()))
        .orElseThrow();

      log.info("도달2-out");
    } else {
      if (memberId.equals(NON_USER)) {
        location = this.locationRepository.getLocationByObjectId(365L)
          .orElseThrow();
      } else {
        List<Location> memberWishTownList = this.memberWishTownRepository.getMemberWishTownsByMemberId(
            memberId)
          .stream().map(MemberWishTown::getLocation).collect(Collectors.toList());

        Collections.shuffle(memberWishTownList);
        location = memberWishTownList.get(0);
      }
    }

    return locationConverter.toLocationInfo(location).get();
  }

  public HashMap<String, Object> getInfraPlaceInfo(
    Long object_id,
    String category
  ) {

    HashMap<String, Object> infraPlaceListHashMap = new HashMap<>();

    List<Infra> infraList = infraRepository.findInfraByCategory(category);

    for (int i = 0; i < infraList.size(); i++) {
      List<InfraPlaceDto> infraPlaceList = placeRepository.findByInfra(
          object_id, category, infraList.get(i).getSubCategory())
        .stream()
        .map(InfraPlaceDto::new)
        .collect(Collectors.toList());
      ;

      infraPlaceListHashMap.put(infraList.get(i).getSubCategoryName(), infraPlaceList);
    }

    return infraPlaceListHashMap;
  }

  public HashMap<String, Object> getThemePlaceInfo(
    Long object_id,
    String category
  ) {

    HashMap<String, Object> listHashMap = new HashMap<>();

    List<Theme> themeList = themeRepository.findThemeByCategory(category);

    for (int i = 0; i < themeList.size(); i++) {
      List<ThemePlaceDto> themePlaceList = placeRepository.findByTheme(
          object_id, category, themeList.get(i).getSubCategory())
        .stream()
        .map(ThemePlaceDto::new)
        .collect(Collectors.toList());
      ;

      listHashMap.put(themeList.get(i).getSubCategoryName(), themePlaceList);
    }

    return listHashMap;
  }

}
