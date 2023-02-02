package yapp.domain.townMap.service;

import static yapp.common.config.Const.NON_USER;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yapp.common.domain.Location;
import yapp.common.repository.LocationRepository;
import yapp.domain.member.entitiy.MemberWishTown;
import yapp.domain.member.repository.MemberWishTownRepository;
import yapp.domain.townMap.converter.LocationConverter;
import yapp.domain.townMap.dto.response.LocationInfoResponse;
import yapp.domain.townMap.repository.InfraRepository;

@Service
@Slf4j
public class TownMapService {

  private final LocationRepository locationRepository;
  private final MemberWishTownRepository memberWishTownRepository;
  private final LocationConverter locationConverter;

  public TownMapService(
    LocationRepository locationRepository,
    MemberWishTownRepository memberWishTownRepository,
    LocationConverter locationConverter
  ) {
    this.locationRepository = locationRepository;
    this.memberWishTownRepository = memberWishTownRepository;
    this.locationConverter = locationConverter;
  }

  public LocationInfoResponse getLocationInfo (String memberId) {

    Location location;

    if (memberId.equals(NON_USER)){
      location = this.locationRepository.getLocationByObjectId(365L)
        .orElseThrow();
    }
    else {
      List<Location> memberWishTownList = this.memberWishTownRepository.getMemberWishTownsByMemberId(
        memberId).stream().map(MemberWishTown::getLocation).collect(Collectors.toList());

      Collections.shuffle(memberWishTownList);
      location = memberWishTownList.get(0);
    }

    return locationConverter.toLocationInfo(location).get();
  }

}
