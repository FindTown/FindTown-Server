package yapp.domain.town.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import yapp.domain.town.entity.TownSubway;
import yapp.domain.townMap.entity.Place;

@Getter
@Setter
public class TownDto {
  private Long objectId;
  private Place place;
  private TownSubway townSubway;
  private String[] moods;
  private String reliefYn;
  private int lifeRate;
  private int crimeRate;
  private int trafficRate;
  private String townIntroduction;

  private boolean wishTown = false;
  private Set<Place> placeSet;
  private Set<TownSubway> townSubwaySet;
  private double safetyRate;

  @QueryProjection
  public TownDto(
          Long objectId,
          Place place,
          TownSubway townSubway,
          String reliefYn,
          int lifeRate,
          int crimeRate,
          int trafficRate,
          String townIntroduction
  ) {
    this.objectId = objectId;
    this.place = place;
    this.townSubway = townSubway;
    this.reliefYn = reliefYn;
    this.lifeRate = lifeRate;
    this.crimeRate = crimeRate;
    this.trafficRate = trafficRate;
    this.townIntroduction = townIntroduction;
  }
}
