package yapp.domain.townMap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yapp.domain.townMap.entity.Place;

@Schema(description = "인프라 장소 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InfraPlaceDto {

  @Schema(description = "장소 이름")
  private String name;

  @Schema(description = "장소 x 좌표")
  private Double x;

  @Schema(description = "장소 y 좌표")
  private Double y;

  @Builder
  public InfraPlaceDto(Place place){
    this.name = place.getName();
    this.x = place.getX();
    this.y = place.getY();
  }
  
}
