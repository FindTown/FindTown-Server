package yapp.domain.townMap.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import yapp.domain.townMap.entity.Infra;

@Getter
@Setter
@NoArgsConstructor
public class InfraPlaceDto {

  private String name;
  private String address;
  private Double x;
  private Double y;
  private Infra infra;

  @QueryProjection
  public InfraPlaceDto(
    String name,
    String address,
    Double x,
    Double y,
    Infra infra
  ){
    this.name = name;
    this.address = address;
    this.x = x;
    this.y = y;
    this.infra = infra;
  }
  
}
