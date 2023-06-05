package yapp.domain.town.converter;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import yapp.domain.town.dto.TownDetailDto;
import yapp.domain.town.dto.TownDto;
import yapp.domain.town.dto.response.TownFilterResponse;
import yapp.domain.town.dto.response.TownInfoResponse;
import yapp.domain.town.dto.response.TownSearchResponse;
import yapp.domain.town.entity.Mood;
import yapp.domain.town.entity.Subway;
import yapp.domain.town.entity.Town;
import yapp.domain.town.entity.TownHotPlace;

@Component
@RequiredArgsConstructor
public class TownConverter {

  public TownFilterResponse toFilterTown(
          TownDto townDto,
          Set<Long> memberWishTownList
  ) {
    return TownFilterResponse.builder()
            .objectId(townDto.getObjectId())
            .moods(townDto.getMoods())
            .safetyRate(townDto.getSafetyRate())
            .lifeRate(townDto.getLifeRate())
            .crimeRate(townDto.getCrimeRate())
            .trafficRate(townDto.getTrafficRate())
            .reliefYn(townDto.getReliefYn())
            .wishTown(memberWishTownList.contains(townDto.getObjectId()))
            .build();
  }

  public TownSearchResponse toSearchTown(
          Town town,
          Set<Long> memberWishTownList,
          String[] moods
  ) {
    return TownSearchResponse.builder()
            .objectId(town.getObjectId())
            .moods(moods)
            .wishTown(memberWishTownList.contains(town.getObjectId()))
            .build();
  }

  public TownInfoResponse toTownDetailInfo(
          TownDetailDto townDetailDto,
          Set<Long> memberWishTownList
  ) {
    return TownInfoResponse.builder()
            .objectId(townDetailDto.getObjectId())
            .townExplanation(townDetailDto.getTownDescribe())
            .reliefYn(townDetailDto.getReliefYn().equals("Y") ? "안심마을보안관 활동지" : "")
            .lifeRate(townDetailDto.getLifeRate())
            .crimeRate(townDetailDto.getCrimeRate())
            .trafficRate(townDetailDto.getTrafficRate())
            .cleanlinessRank(townDetailDto.getCleanlinessRank())
            .liveRank(townDetailDto.getLiveRank())
            .popularTownRate(townDetailDto.getTownPopular().getPopularRate())
            .popularGeneration(townDetailDto.getTownPopular().getPopularGeneration())
            .townSubwayList(townDetailDto.getTownSubwayList()
                    .stream()
                    .map(Subway::getLineNum)
                    .distinct()
                    .collect(
                            Collectors.toList()))
            .townMoodList(
                    townDetailDto.getTownMoodList()
                            .stream()
                            .map(Mood::getKeyword)
                            .distinct()
                            .collect(
                                    Collectors.toList()))
            .townHotPlaceList(townDetailDto.getTownHotPlaceList()
                    .stream()
                    .map(TownHotPlace::getHotPlaceNm)
                    .distinct()
                    .collect(
                            Collectors.toList()))
            .wishTown(memberWishTownList.contains(townDetailDto.getObjectId()))
            .build();
  }

}
