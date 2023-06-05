package yapp.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberWishTownDto {
  private Long objectId;
  private String[] moods;
  private String sggnm;

  public MemberWishTownDto(
          Long objectId,
          String[] moods,
          String sggnm
  ) {
    this.objectId = objectId;
    this.moods = moods;
    this.sggnm = sggnm;
  }
}
