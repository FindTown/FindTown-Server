package yapp.domain.townMap.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LocationInfoResponse {
  private Long object_id;
  private String adm_nm;
  private Double[][] coordinates;
}
