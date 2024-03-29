package yapp.domain.town.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import yapp.domain.town.entity.Mood;
import yapp.domain.town.entity.Subway;
import yapp.domain.town.entity.TownHotPlace;
import yapp.domain.town.entity.TownPopular;

@Getter
@Setter
@NoArgsConstructor
public class TownDetailDto {

  private Long objectId;
  private String townDescribe;
  private String reliefYn;
  private List<Subway> townSubwayList;
  private List<Mood> townMoodList;
  private TownPopular townPopular;
  private List<TownHotPlace> townHotPlaceList;
  private int lifeRate;
  private int crimeRate;
  private int trafficRate;
  private int liveRank;
  private String cleanlinessRank;

  public TownDetailDto(
    Long objectId,
    String townDescribe,
    String reliefYn,
    List<Subway> townSubwayList,
    List<Mood> townMoodList,
    TownPopular townPopular,
    List<TownHotPlace> townHotPlace,
    int lifeRate,
    int crimeRate,
    int trafficRate,
    int liveRank,
    String cleanlinessRank
  ) {
    this.objectId = objectId;
    this.townDescribe = townDescribe;
    this.reliefYn = reliefYn;
    this.townSubwayList = townSubwayList;
    this.townMoodList = townMoodList;
    this.townPopular = townPopular;
    this.townHotPlaceList = townHotPlace;
    this.lifeRate = lifeRate;
    this.crimeRate = crimeRate;
    this.trafficRate = trafficRate;
    this.liveRank = liveRank;
    this.cleanlinessRank = cleanlinessRank;
  }
}
