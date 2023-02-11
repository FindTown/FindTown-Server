package yapp.domain.town.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TownFilterResponse {
  private Long objectId;
  private String townIntroduction;
  private double safetyRate;
  private int lifeRate;
  private int crimeRate;
  private int trafficRate;
  private String reliefYn;
  private boolean wishTown;
  private int liveRank;            //살기 좋은 동네
  private String cleanlinessRank;     //청결도 -> TOP10 반환
}
