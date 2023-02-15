package yapp.domain.townMap.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InfraPlaceResponse {
  private String name;
  private String address;
  private Double x;
  private Double y;
  private String subCategory;
}
