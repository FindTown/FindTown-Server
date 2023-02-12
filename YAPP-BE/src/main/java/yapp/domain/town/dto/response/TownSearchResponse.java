package yapp.domain.town.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TownSearchResponse {
  private Long objectId;
  private String townIntroduction;
  private boolean wishTown;
}
