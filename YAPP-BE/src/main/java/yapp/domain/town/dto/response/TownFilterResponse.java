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
}
