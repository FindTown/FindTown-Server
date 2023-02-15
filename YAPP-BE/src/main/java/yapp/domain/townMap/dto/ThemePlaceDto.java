package yapp.domain.townMap.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import yapp.domain.townMap.entity.Theme;

@Getter
@Setter
@NoArgsConstructor
public class ThemePlaceDto {

  private String name;
  private String address;
  private Double x;
  private Double y;
  private Theme theme;

  @QueryProjection
  public ThemePlaceDto(
    String name,
    String address,
    Double x,
    Double y,
    Theme theme
  ){
    this.name = name;
    this.address = address;
    this.x = x;
    this.y = y;
    this.theme = theme;
  }

}
