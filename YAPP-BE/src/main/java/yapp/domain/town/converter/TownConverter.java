package yapp.domain.town.converter;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import yapp.domain.town.dto.TownDto;
import yapp.domain.town.dto.response.TownFilterResponse;

@Component
@RequiredArgsConstructor
public class TownConverter {

  public TownFilterResponse toFilterTown(
    TownDto townDto,
    Set<Long> memberWishTownList
  ) {
    return TownFilterResponse.builder()
      .objectId(townDto.getObjectId())
      .townIntroduction(townDto.getTownIntroduction())
      .safetyRate(townDto.getSafetyRate())
      .lifeRate(townDto.getLifeRate())
      .crimeRate(townDto.getCrimeRate())
      .trafficRate(townDto.getTrafficRate())
      .reliefYn(townDto.getReliefYn())
      .wishTown(memberWishTownList.contains(townDto.getObjectId()))
      .build();
  }

}
